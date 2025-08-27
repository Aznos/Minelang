package parse

import lexer.Token

/**
 * Consumes tokens and produces a [Program]
 */
class Parser(private val tokens: List<Token>) {
    private var i = 0

    private fun peek(): Token = tokens[i]
    private fun atEof(): Boolean = peek().kind is Token.Kind.EOF
    private fun advance(): Token = tokens[i++]

    private fun match(kind: Token.Kind): Boolean {
        if(peek().kind == kind) {
            i++
            return true
        }

        return false
    }

    private fun consumeEols() {
        while(peek().kind is Token.Kind.EOL) {
            i++
        }
    }

    private fun expectKeyword(k: Token.Kind.Keyword): Token {
        val t = advance()
        val ok = (t.kind as? Token.Kind.Keyword) == k
        if(!ok) errorAt(t, "Expected keyword '${k.name.lowercase()}")
        return t
    }

    private fun expectIdent(): String {
        val t = advance()
        val id = (t.kind as? Token.Kind.Ident)?.text
        if(id == null) errorAt(t," Expected identifier")
        return id!!
    }

    private fun expectInt(): Int {
        val t = advance()
        val n = (t.kind as? Token.Kind.IntLit)?.value

        if(n == null) errorAt(t, "Expected integer literal")
        if(n !in 1..36) errorAt(t, "Slot index out of range: $n (must be 1-36)")

        return n.toInt()
    }

    private fun errorAt(t: Token, msg: String): Nothing {
        throw ParseException("${t.line}:${t.col}: $msg")
    }

    /**
     * Parse a full [Program]
     */
    fun parseProgram(): Program {
        val ins = mutableListOf<Instr>()
        consumeEols()
        while(!atEof()) {
            ins += parseStmt()
            consumeEols()
        }

        return Program(ins)
    }

    private fun parseStmt(): Instr {
        val t = peek()
        return when(t.kind) {
            Token.Kind.Keyword.SAY -> parseSay()
            Token.Kind.Keyword.PLACE -> parsePlace()
            Token.Kind.Keyword.CRAFT -> parseCraft()
            is Token.Kind.EOL -> { advance(); parseStmt() }
            is Token.Kind.EOF -> errorAt(t, "Unexpected EOF")
            else -> errorAt(t, "Unexpected token: ${t.kind}")
        }
    }

    private fun parseSay(): Instr {
        expectKeyword(Token.Kind.Keyword.SAY)

        return if((peek().kind as? Token.Kind.Keyword) == Token.Kind.Keyword.SLOT) {
            expectKeyword(Token.Kind.Keyword.SLOT)
            val n = expectInt()
            val toStr = parseOptionalToString()
            Instr.SaySlot(n, toStr)
        } else {
            val item = expectIdent()
            val toStr = parseOptionalToString()
            Instr.SayItem(item, toStr)
        }
    }

    private fun parsePlace(): Instr {
        expectKeyword(Token.Kind.Keyword.PLACE)
        val item = expectIdent()

        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)

        val n = expectInt()
        return Instr.Place(item, n)
    }

    private fun parseCraft(): Instr {
        expectKeyword(Token.Kind.Keyword.CRAFT)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val a = expectInt()

        expectKeyword(Token.Kind.Keyword.WITH)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val b = expectInt()

        expectKeyword(Token.Kind.Keyword.INTO)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val c = expectInt()

        return Instr.CraftAdd(a, b, c)
    }

    private fun parseOptionalToString(): Boolean {
        return if((peek().kind as? Token.Kind.Keyword) == Token.Kind.Keyword.TO) {
            advance()
            expectKeyword(Token.Kind.Keyword.STRING)
            true
        } else false
    }
}

/**
 * Thrown when parsing fails with a human-readable message
 */
class ParseException(message: String) : RuntimeException(message)
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
            is Token.Kind.EOF -> errorAt(t, "Unexpected EOF")
            is Token.Kind.EOL -> { advance(); parseStmt() }
            else -> errorAt(t, "Expected a statement")
        }
    }

    private fun parseSay(): Instr {
        expectKeyword(Token.Kind.Keyword.SAY)

        val item = expectIdent()
        var toString = false
        if((peek().kind as? Token.Kind.Keyword) == Token.Kind.Keyword.TO) {
            advance()
            expectKeyword(Token.Kind.Keyword.STRING)
            toString = true
        }

        return Instr.Say(item, toString)
    }
}

/**
 * Thrown when parsing fails with a human-readable message
 */
class ParseException(message: String) : RuntimeException(message)
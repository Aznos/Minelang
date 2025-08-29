package parse

import jdk.internal.org.jline.keymap.KeyMap.key
import lexer.Token
import parse.Operand.*
import java.security.Key
import kotlin.math.exp
import kotlin.math.round

/**
 * Consumes tokens and produces a [Program]
 */
class Parser(private val tokens: List<Token>) {
    private var i = 0

    private fun peek(): Token = tokens[i]
    private fun atEof(): Boolean = peek().kind is Token.Kind.EOF
    private fun advance(): Token = tokens[i++]

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
            Token.Kind.Keyword.ASK -> parseAsk()
            Token.Kind.Keyword.PLACE -> {

                val t1 = tokens.getOrNull(i + 1)?.kind
                if(t1 == Token.Kind.Keyword.SACK) parsePlaceSack() else parsePlace()
            }
            Token.Kind.Keyword.CRAFT -> parseCraft()
            Token.Kind.Keyword.SHEAR -> parseShear()
            Token.Kind.Keyword.SMITH -> parseSmith()
            Token.Kind.Keyword.DISENCHANT -> parseDisenchant()
            Token.Kind.Keyword.REDSTONE -> parseRedstone()
            Token.Kind.Keyword.MINE -> parseMine()
            Token.Kind.Keyword.SMELT -> parseSmelt()
            Token.Kind.Keyword.TRAVEL -> parseTravel()
            Token.Kind.Keyword.LENGTH -> parseLength()
            Token.Kind.Keyword.TRADE -> parseTrade()
            Token.Kind.Keyword.SPRINT -> parseSprint()
            Token.Kind.Keyword.SNEAK -> parseSneak()
            Token.Kind.Keyword.BREW -> parseBrewInto()
            Token.Kind.Keyword.SLEEP -> parseSleep()
            Token.Kind.Keyword.SCRIBE -> parseScribe()
            Token.Kind.Keyword.BIND -> parseBind()
            Token.Kind.Keyword.LOOM -> parseLoom()

            is Token.Kind.EOL -> { advance(); parseStmt() }
            is Token.Kind.EOF -> errorAt(t, "Unexpected EOF")
            else -> errorAt(t, "Unexpected token: ${t.kind}")
        }
    }

    private fun parseSay(): Instr {
        expectKeyword(Token.Kind.Keyword.SAY)
        val operand = parseOperand()
        return Instr.SayExpr(operand)
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

        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val c = expectInt()

        return Instr.CraftAdd(a, b, c)
    }

    private fun parseShear(): Instr {
        expectKeyword(Token.Kind.Keyword.SHEAR)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val a = expectInt()

        expectKeyword(Token.Kind.Keyword.FROM)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val b = expectInt()

        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val c = expectInt()

        return Instr.ShearSub(a, b, c)
    }

    private fun parseSmith(): Instr {
        expectKeyword(Token.Kind.Keyword.SMITH)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val a = expectInt()

        expectKeyword(Token.Kind.Keyword.WITH)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val b = expectInt()

        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val c = expectInt()

        return Instr.SmithMul(a, b, c)
    }

    private fun parseDisenchant(): Instr {
        expectKeyword(Token.Kind.Keyword.DISENCHANT)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val a = expectInt()

        expectKeyword(Token.Kind.Keyword.BY)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val b = expectInt()

        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val c = expectInt()

        return Instr.DisenchantDiv(a, b, c)
    }

    private fun parseOperand(): Operand {
        return when(val k = peek().kind) {
            is Token.Kind.Keyword -> {
                when(k) {
                    Token.Kind.Keyword.SLOT -> {
                        advance()
                        Slot(expectInt())
                    }

                    Token.Kind.Keyword.HARVEST -> {
                        advance()
                        expectKeyword(Token.Kind.Keyword.SLOT)
                        val sackSlot = expectInt()
                        expectKeyword(Token.Kind.Keyword.AT)
                        val idx = parseIndexOperand()

                        Harvest(sackSlot, idx)
                    }

                    Token.Kind.Keyword.BREW -> parseBrewOperand()
                    else -> errorAt(peek(), "Expected operand, got keyword '${k.name.lowercase()}'")
                }
            }

            is Token.Kind.IntLit -> {
                val v = (advance().kind as Token.Kind.IntLit).value
                Number(v.toInt())
            }

            is Token.Kind.Ident -> {
                val name = (advance().kind as Token.Kind.Ident).text
                Item(name)
            }

            Token.Kind.LBRACK -> parseSackLiteralOperand()
            Token.Kind.EOF -> errorAt(peek(), "Expected operand, got EOF")
            Token.Kind.EOL -> errorAt(peek(), "Expected operand, got EOL")

            else -> errorAt(peek(), "Expected operand, got $k")
        }
    }

    private fun parseSackLiteralOperand(): SackLiteral {
        val lb = advance()
        if(lb.kind !is Token.Kind.LBRACK) errorAt(lb, "Expected '['")
        val items = mutableListOf<String>()
        consumeEols()

        if(peek().kind !is Token.Kind.RBRACK) {
            items += expectIdent()
            consumeEols()
            while(peek().kind is Token.Kind.COMMA) {
                advance()
                consumeEols()
                items += expectIdent()
                consumeEols()
            }
        }

        val rb = advance()
        if(rb.kind !is Token.Kind.RBRACK) errorAt(rb, "Expected ']'")
        return SackLiteral(items)
    }

    private data class BrewOpts(
        var scale: Int? = null,
        var rounding: Rounding? = null,
        var sawCauldron: Boolean = false,
        var sawGrindstone: Boolean = false
    )

    private fun parseBrewOptions(opts: BrewOpts) {
        while((peek().kind as? Token.Kind.Keyword) == Token.Kind.Keyword.WITH) {
            advance()
            when(peek().kind as? Token.Kind.Keyword) {
                Token.Kind.Keyword.CAULDRON -> {
                    if(opts.sawCauldron) errorAt(peek(), "Duplicate 'with cauldron' clause")
                    advance()
                    expectKeyword(Token.Kind.Keyword.SCALE)
                    opts.scale = expectInt()
                    opts.sawCauldron = true
                }

                Token.Kind.Keyword.GRINDSTONE -> {
                    if(opts.sawGrindstone) errorAt(peek(), "Duplicate 'with grindstone' clause")
                    advance()
                    opts.rounding = parseRounding()
                    opts.sawGrindstone = true
                }

                else -> errorAt(peek(), "Expected 'cauldron' or 'grindstone' after 'with'")
            }
        }
    }

    private fun parseBrewOperand(): Brew {
        expectKeyword(Token.Kind.Keyword.BREW)
        val value = parseOperand()
        expectKeyword(Token.Kind.Keyword.AS)

        val target = parseBrewType()
        val opts = BrewOpts()
        parseBrewOptions(opts)
        return Brew(value, target, opts.rounding, opts.scale)
    }

    private fun parseBrewInto(): Instr.BrewInto {
        expectKeyword(Token.Kind.Keyword.BREW)
        val value = parseOperand()
        expectKeyword(Token.Kind.Keyword.AS)

        val target = parseBrewType()
        val opts = BrewOpts()

        parseBrewOptions(opts)

        val k = (peek().kind as? Token.Kind.Keyword)
        if(k != Token.Kind.Keyword.IN) errorAt(peek(), "Expected 'in' after brew target type")
        advance()
        expectKeyword(Token.Kind.Keyword.SLOT)
        val dst = expectInt()

        parseBrewOptions(opts)
        return Instr.BrewInto(value, target, dst, opts.rounding, opts.scale)
    }

    private fun parseBrewType(): BrewType {
        val t = advance()
        val kw = t.kind as? Token.Kind.Keyword ?: errorAt(t, "Expected brew type, got ${t.kind}")
        return when(kw) {
            Token.Kind.Keyword.INT_T -> BrewType.INT
            Token.Kind.Keyword.RAT_T -> BrewType.RAT
            Token.Kind.Keyword.FLOAT_T -> BrewType.FLOAT
            Token.Kind.Keyword.STRING_T -> BrewType.STRING
            else -> errorAt(t, "Expected brew type, got keyword '${kw.name.lowercase()}'")
        }
    }

    private fun parseRounding(): Rounding {
        val t = advance()
        val kw = t.kind as? Token.Kind.Keyword ?: errorAt(t, "Expected rounding mode, got ${t.kind}")
        return when(kw) {
            Token.Kind.Keyword.FLOOR -> Rounding.FLOOR
            Token.Kind.Keyword.CEIL -> Rounding.CEIL
            Token.Kind.Keyword.ROUND -> Rounding.ROUND
            Token.Kind.Keyword.TRUNC -> Rounding.TRUNC
            else -> errorAt(t, "Expected rounding mode, got keyword '${kw.name.lowercase()}'")
        }
    }

    private fun parseCmp(): Cmp {
        val t = advance()
        val kw = t.kind as? Token.Kind.Keyword ?: errorAt(t, "Expected comparison operator, got ${t.kind}")
        return when(kw) {
            Token.Kind.Keyword.BEDROCK -> Cmp.BEDROCK_EQ
            Token.Kind.Keyword.TNT -> Cmp.TNT_NE
            else -> errorAt(t, "Expected comparison operator, got keyword '${kw.name.lowercase()}'")
        }
    }

    private fun parseCondition(): Condition {
        val left = parseOperand()
        val cmp = parseCmp()
        val right = parseOperand()
        return Condition(left, cmp, right)
    }

    private fun parseBlock(until: Set<Token.Kind.Keyword>): List<Instr> {
        val body = mutableListOf<Instr>()
        consumeEols()

        while(true) {
            val k = (peek().kind as? Token.Kind.Keyword)
            if(k != null && k in until) break
            if(peek().kind is Token.Kind.EOF) errorAt(peek(), "Unterminated block")

            body += parseStmt()
            consumeEols()
        }

        return body
    }

    private fun parseRedstone(): Instr {
        expectKeyword(Token.Kind.Keyword.REDSTONE)
        val cond = parseCondition()
        expectKeyword(Token.Kind.Keyword.THEN)

        val thenBlock = parseBlock(setOf(Token.Kind.Keyword.ELSE, Token.Kind.Keyword.END))
        val elseBlock = if((peek().kind as? Token.Kind.Keyword) == Token.Kind.Keyword.ELSE) {
            advance()
            val block = parseBlock(setOf(Token.Kind.Keyword.END))
            block
        } else null

        expectKeyword(Token.Kind.Keyword.END)
        return Instr.Redstone(cond, thenBlock, elseBlock)
    }

    private fun parseMine(): Instr {
        expectKeyword(Token.Kind.Keyword.MINE)
        val cond = parseCondition()

        expectKeyword(Token.Kind.Keyword.DO)
        val body = parseBlock(setOf(Token.Kind.Keyword.END))

        expectKeyword(Token.Kind.Keyword.END)
        return Instr.Mine(cond, body)
    }

    private fun parseSmelt(): Instr {
        expectKeyword(Token.Kind.Keyword.SMELT)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val n = expectInt()

        expectKeyword(Token.Kind.Keyword.TIMES)
        expectKeyword(Token.Kind.Keyword.DO)

        val body = parseBlock(setOf(Token.Kind.Keyword.END))
        expectKeyword(Token.Kind.Keyword.END)

        return Instr.Smelt(n, body)
    }

    private fun parseTravel(): Instr {
        expectKeyword(Token.Kind.Keyword.TRAVEL)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val iSlot = expectInt()

        expectKeyword(Token.Kind.Keyword.FROM)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val a = expectInt()

        expectKeyword(Token.Kind.Keyword.TO)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val b = expectInt()

        expectKeyword(Token.Kind.Keyword.DO)
        val body = parseBlock(setOf(Token.Kind.Keyword.END))
        expectKeyword(Token.Kind.Keyword.END)

        return Instr.Travel(iSlot, a, b, body)
    }

    private fun parsePlaceSack(): Instr {
        expectKeyword(Token.Kind.Keyword.PLACE)
        expectKeyword(Token.Kind.Keyword.SACK)
        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val slot = expectInt()

        expectKeyword(Token.Kind.Keyword.CONTAINS)
        val lb = advance()
        if(lb.kind !is Token.Kind.LBRACK) errorAt(lb, "Expected '[' to start sack item list")

        val items = mutableListOf<String>()
        consumeEols()
        if(peek().kind !is Token.Kind.RBRACK) {
            items += expectIdent()
            consumeEols()
            while(peek().kind is Token.Kind.COMMA) {
                advance()
                consumeEols()
                items += expectIdent()
                consumeEols()
            }
        }

        val rb = advance()
        if(rb.kind !is Token.Kind.RBRACK) errorAt(rb, "Expected ']' to end sack item list")

        return Instr.PlaceSack(slot, items)
    }

    private fun parseIndexOperand(): Operand {
        return when(peek().kind) {
            Token.Kind.Keyword.SLOT -> {
                advance()
                Operand.Slot(expectInt())
            }

            is Token.Kind.IntLit -> {
                val v = (advance().kind as Token.Kind.IntLit).value
                Operand.Number(v.toInt())
            }

            else -> errorAt(peek(), "Expected slot or integer literal")
        }
    }

    private fun parseLength(): Instr {
        expectKeyword(Token.Kind.Keyword.LENGTH)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val sackSlot = expectInt()

        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val dst = expectInt()

        return Instr.Length(sackSlot, dst)
    }

    private fun parseTrade(): Instr {
        expectKeyword(Token.Kind.Keyword.TRADE)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val sackSlot = expectInt()

        expectKeyword(Token.Kind.Keyword.AT)
        val idx = parseIndexOperand()

        expectKeyword(Token.Kind.Keyword.TO)
        val item = expectIdent()

        return Instr.Trade(sackSlot, idx, item)
    }

    private fun parseSprint(): Instr {
        expectKeyword(Token.Kind.Keyword.SPRINT)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val sackSlot = expectInt()

        expectKeyword(Token.Kind.Keyword.WITH)
        val item = expectIdent()

        return Instr.Sprint(sackSlot, item)
    }

    private fun parseSneak(): Instr {
        expectKeyword(Token.Kind.Keyword.SNEAK)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val sackSlot = expectInt()

        return Instr.Sneak(sackSlot)
    }

    private fun parseAsk(): Instr {
        expectKeyword(Token.Kind.Keyword.ASK)
        val prompt = parseOperand()

        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val slot = expectInt()

        return Instr.Ask(prompt, slot)
    }

    private fun parseSleep(): Instr {
        expectKeyword(Token.Kind.Keyword.SLEEP)
        val t = advance()
        val n = (t.kind as? Token.Kind.IntLit)?.value ?: errorAt(t, "Expected integer literal")
        return Instr.Sleep(n.toInt())
    }

    private fun parseScribe(): Instr {
        expectKeyword(Token.Kind.Keyword.SCRIBE)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val src = expectInt()
        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val dst = expectInt()

        return Instr.Scribe(src, dst)
    }

    private fun parseBind(): Instr {
        expectKeyword(Token.Kind.Keyword.BIND)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val a = expectInt()
        expectKeyword(Token.Kind.Keyword.WITH)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val b = expectInt()
        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val dst = expectInt()

        return Instr.Bind(a, b, dst)
    }

    private fun parseLoom(): Instr {
        expectKeyword(Token.Kind.Keyword.LOOM)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val sack = expectInt()
        expectKeyword(Token.Kind.Keyword.AT)
        val idx = parseIndexOperand()
        expectKeyword(Token.Kind.Keyword.IN)
        expectKeyword(Token.Kind.Keyword.SLOT)
        val dst = expectInt()

        return Instr.Loom(sack, idx, dst)
    }
}

/**
 * Thrown when parsing fails with a human-readable message
 */
class ParseException(message: String) : RuntimeException(message)
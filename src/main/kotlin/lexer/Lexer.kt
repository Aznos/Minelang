package lexer

import io.Source

/**
 * Converts raw [Source] ext into a stream of [Token]s
 */
class Lexer(private val source: Source) {
    private val keywords = mapOf(
        "say" to Token.Kind.Keyword.SAY,
        "ask" to Token.Kind.Keyword.ASK,
        "place" to Token.Kind.Keyword.PLACE,
        "in" to Token.Kind.Keyword.IN,
        "to" to Token.Kind.Keyword.TO,
        "slot" to Token.Kind.Keyword.SLOT,
        "craft" to Token.Kind.Keyword.CRAFT,
        "with" to Token.Kind.Keyword.WITH,
        "shear" to Token.Kind.Keyword.SHEAR,
        "from" to Token.Kind.Keyword.FROM,
        "disenchant" to Token.Kind.Keyword.DISENCHANT,
        "by" to Token.Kind.Keyword.BY,
        "smith" to Token.Kind.Keyword.SMITH,

        "redstone" to Token.Kind.Keyword.REDSTONE,
        "then" to Token.Kind.Keyword.THEN,
        "else" to Token.Kind.Keyword.ELSE,
        "end" to Token.Kind.Keyword.END,

        "mine" to Token.Kind.Keyword.MINE,
        "do" to Token.Kind.Keyword.DO,

        "smelt" to Token.Kind.Keyword.SMELT,
        "times" to Token.Kind.Keyword.TIMES,

        "travel" to Token.Kind.Keyword.TRAVEL,
        "bedrock" to Token.Kind.Keyword.BEDROCK,
        "tnt" to Token.Kind.Keyword.TNT,

        "sack" to Token.Kind.Keyword.SACK,
        "contains" to Token.Kind.Keyword.CONTAINS,
        "harvest" to Token.Kind.Keyword.HARVEST,
        "at" to Token.Kind.Keyword.AT,
        "length" to Token.Kind.Keyword.LENGTH,
        "trade" to Token.Kind.Keyword.TRADE,
        "sprint" to Token.Kind.Keyword.SPRINT,
        "sneak" to Token.Kind.Keyword.SNEAK,

        "brew" to Token.Kind.Keyword.BREW,
        "as" to Token.Kind.Keyword.AS,
        "grindstone" to Token.Kind.Keyword.GRINDSTONE,
        "floor" to Token.Kind.Keyword.FLOOR,
        "ceil" to Token.Kind.Keyword.CEIL,
        "round" to Token.Kind.Keyword.ROUND,
        "trunc" to Token.Kind.Keyword.TRUNC,
        "cauldron" to Token.Kind.Keyword.CAULDRON,
        "scale" to Token.Kind.Keyword.SCALE,

        "int" to Token.Kind.Keyword.INT_T,
        "rat" to Token.Kind.Keyword.RAT_T,
        "float" to Token.Kind.Keyword.FLOAT_T,
        "string" to Token.Kind.Keyword.STRING_T,

        "sleep" to Token.Kind.Keyword.SLEEP,
        "scribe" to Token.Kind.Keyword.SCRIBE,
        "bind" to Token.Kind.Keyword.BIND,
        "loom" to Token.Kind.Keyword.LOOM,
        "flip" to Token.Kind.Keyword.FLIP,

        "enchant" to Token.Kind.Keyword.ENCHANT,
        "lower" to Token.Kind.Keyword.LOWER,
        "upper" to Token.Kind.Keyword.UPPER,

        "chest" to Token.Kind.Keyword.CHEST,
        "key" to Token.Kind.Keyword.KEY,
        "value" to Token.Kind.Keyword.VALUE,
        "stash" to Token.Kind.Keyword.STASH,
        "raid" to Token.Kind.Keyword.RAID,

        "command" to Token.Kind.Keyword.COMMAND,
        "activate" to Token.Kind.Keyword.ACTIVATE,
    )

    private val text = source.content
    private var i = 0
    private var line = 1
    private var col = 1

    private fun atEnd() = i >= text.length
    private fun peek() = if(atEnd()) '\u0000' else text[i]
    private fun peekNext(): Char = if(i + 1 >= text.length) '\u0000' else text[i + 1]
    private fun advance(): Char {
        val c = peek()
        i++
        if(c == '\n') {
            line++
            col = 1
        } else {
            col++
        }

        return c
    }

    private fun make(kind: Token.Kind, l: Int = line, c: Int = col) = Token(kind, l, c)
    private fun isDigit(c: Char) = c in '0'..'9'
    private fun isIdentStart(c: Char) = c == '_' || c.isLetter()
    private fun isIdentPart(c: Char) = c == '_' || c.isLetterOrDigit()

    private fun scanLineComment() {
        while(!atEnd() && peek() != '\n') advance()
    }

    private fun scanNumber(startLine: Int, startCol: Int): Token {
        val sb = StringBuilder()
        while(isDigit(peek())) sb.append(advance())

        val v = sb.toString().toLong()
        return Token(Token.Kind.IntLit(v), startLine, startCol)
    }

    private fun scanIdentOrKeyword(startLine: Int, startCol: Int): Token {
        val sb = StringBuilder()
        sb.append(advance())
        while(isIdentPart(peek())) sb.append(advance())

        val word = sb.toString()
        val kw = keywords[word.lowercase()]
        return if(kw != null) Token(kw, startLine, startCol)
        else Token(Token.Kind.Ident(word), startLine, startCol)
    }

    /**
     * Lexes the entire source into a list of tokens
     *
     * @return list ending with a single [Token.Kind.EOF]
     */
    fun lexAll(): List<Token> {
        val out = mutableListOf<Token>()
        while(!atEnd()) {
            val c = peek()
            when {
                c == ' ' || c == '\t' || c == '\r' -> advance()
                c == '\n' -> {
                    advance()
                    out += make(Token.Kind.EOL)
                }

                c == '/' && peekNext() == '/' -> scanLineComment()
                c == '"' -> {
                    val l = line; val co = col
                    advance()
                    out += scanString(l, co)
                }

                c == '[' -> {
                    advance()
                    out += make(Token.Kind.LBRACK)
                }

                c == ']' -> {
                    advance()
                    out += make(Token.Kind.RBRACK)
                }

                c == ',' -> {
                    advance()
                    out += make(Token.Kind.COMMA)
                }

                isDigit(c) -> {
                    val l = line; val co = col
                    out += scanNumber(l, co)
                }

                isIdentStart(c) -> {
                    val l = line; val co = col
                    out += scanIdentOrKeyword(l, co)
                }

                else -> advance()
            }
        }

        out += Token(Token.Kind.EOF, line, col)
        return out
    }

    private fun scanString(startLine: Int, startCol: Int): Token {
        val sb = StringBuilder()
        while(!atEnd()) {
            val c = advance()
            when(c) {
                '"' -> return make(Token.Kind.StringLit(sb.toString()), startLine, startCol)
                '\\' -> {
                    if(atEnd()) break
                    when(val e = advance()) {
                        '\\' -> sb.append('\\')
                        '"' -> sb.append('"')
                        'n' -> sb.append('\n')
                        'r' -> sb.append('\r')
                        't' -> sb.append('\t')
                        'x' -> {
                            val h1 = if(!atEnd()) advance() else '\u0000'
                            val h2 = if(!atEnd()) advance() else '\u0000'
                            val hex = "$h1$h2"
                            val v = hex.toIntOrNull(16) ?: error("Invalid hex escape \\x$hex at $line:$col")
                            sb.append(v.toChar())
                        }
                        else -> error("Invalid escape \\$e at $line:$col")
                    }
                }
                else -> sb.append(c)
            }
        }

        error("Unterminated string starting at $startLine:$startCol")
    }
}
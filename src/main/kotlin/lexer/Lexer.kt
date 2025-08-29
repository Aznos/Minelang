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
    )

    /**
     * Lexes the entire source into a list of tokens
     *
     * @return list ending with a single [Token.Kind.EOF]
     */
    fun lexAll(): List<Token> {
        val out = mutableListOf<Token>()
        source.lines().forEachIndexed { idx, rawLine ->
            val lineNo = idx + 1
            val line = rawLine.substringBefore("//")
            val norm = line
                .replace("[", " [ ")
                .replace("]", " ] ")
                .replace(",", " , ")

            var col = 1
            val parts = norm.split(Regex("\\s+"))
            var pos = 0

            fun advanceCol(tokenText: String) {
                val next = line.indexOf(tokenText, pos)
                col = if(next >= 0) next + 1 else col + tokenText.length + 1
                pos = if(next >= 0) next + tokenText.length else pos + tokenText.length
            }

            for(part in parts) {
                if(part.isBlank()) continue

                val tok = when(part) {
                    "[" -> Token(Token.Kind.LBRACK, lineNo, col)
                    "]" -> Token(Token.Kind.RBRACK, lineNo, col)
                    "," -> Token(Token.Kind.COMMA, lineNo, col)
                    else -> {
                        val lower = part.lowercase()
                        when {
                            lower in keywords -> Token(keywords.getValue(lower), lineNo, col)
                            part.toLongOrNull() != null -> Token(Token.Kind.IntLit(part.toLong()), lineNo, col)
                            else -> Token(Token.Kind.Ident(part), lineNo, col)
                        }
                    }
                }

                out += tok
                advanceCol(part)
            }

            out += Token(Token.Kind.EOL, lineNo, if(line.isEmpty()) 1 else line.length + 1)
        }

        out += Token(Token.Kind.EOF, source.lineCount, 1)
        return out
    }
}
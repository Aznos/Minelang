package lexer

import io.Source

/**
 * Converts raw [Source] ext into a stream of [Token]s
 */
class Lexer(private val source: Source) {
    private val keywords = mapOf(
        "say" to Token.Kind.Keyword.SAY,
        "to" to Token.Kind.Keyword.TO,
        "string" to Token.Kind.Keyword.STRING,
        "place" to Token.Kind.Keyword.PLACE,
        "in" to Token.Kind.Keyword.IN,
        "slot" to Token.Kind.Keyword.SLOT,
        "craft" to Token.Kind.Keyword.CRAFT,
        "with" to Token.Kind.Keyword.WITH,
        "into" to Token.Kind.Keyword.INTO,
        "shear" to Token.Kind.Keyword.SHEAR,
        "from" to Token.Kind.Keyword.FROM,
        "smith" to Token.Kind.Keyword.SMITH,
        "disenchant" to Token.Kind.Keyword.DISENCHANT,
        "by" to Token.Kind.Keyword.BY,
        "redstone" to Token.Kind.Keyword.REDSTONE,
        "then" to Token.Kind.Keyword.THEN,
        "else" to Token.Kind.Keyword.ELSE,
        "end" to Token.Kind.Keyword.END,
        "mine" to Token.Kind.Keyword.MINE,
        "do" to Token.Kind.Keyword.DO,
        "smelt" to Token.Kind.Keyword.SMELT,
        "times" to Token.Kind.Keyword.TIMES,
        "travel" to Token.Kind.Keyword.TRAVEL,
        "from" to Token.Kind.Keyword.FROM,
        "to" to Token.Kind.Keyword.TO,
        "bedrock" to Token.Kind.Keyword.BEDROCK,
        "tnt" to Token.Kind.Keyword.TNT,
        "sack" to Token.Kind.Keyword.SACK,
        "contains" to Token.Kind.Keyword.CONTAINS,
        "harvest" to Token.Kind.Keyword.HARVEST,
        "at" to Token.Kind.Keyword.AT,
        "length" to Token.Kind.Keyword.LENGTH
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
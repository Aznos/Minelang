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
        "disenchant" to Token.Kind.Keyword.DISENCHANT,
        "by" to Token.Kind.Keyword.BY,
        "smith" to Token.Kind.Keyword.SMITH,
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
            var col = 1
            val parts = line.split(Regex("\\s+"))

            for(part in parts) {
                if(part.isBlank()) {
                    col += part.length
                    continue
                }

                val lower = part.lowercase()
                val tok = when {
                    lower in keywords ->
                        Token(keywords.getValue(lower), lineNo, col)
                    part.toLongOrNull() != null ->
                        Token(Token.Kind.IntLit(part.toLong()), lineNo, col)
                    else -> Token(Token.Kind.Ident(part), lineNo, col)
                }

                out += tok
                col += part.length + 1
            }

            out += Token(Token.Kind.EOL, lineNo, if(line.isEmpty()) 1 else line.length + 1)
        }

        out += Token(Token.Kind.EOF, source.lineCount, 1)
        return out
    }
}
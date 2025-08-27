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
                val kind = keywords[lower] ?: Token.Kind.Ident(part)
                out += Token(kind, lineNo, col)
                col += part.length + 1
            }

            out += Token(Token.Kind.EOL, lineNo, if(line.isEmpty()) 1 else line.length + 1)
        }

        out += Token(Token.Kind.EOF, source.lineCount, 1)
        return out
    }
}
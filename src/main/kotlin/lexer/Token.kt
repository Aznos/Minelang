package lexer

/**
 * A lexical token with a kind and source position
 *
 * @property kind The kind of token
 * @property line 1-based line number in the source
 * @property col 1-based column number in the source
 */
data class Token(
    val kind: Kind,
    val line: Int,
    val col: Int
) {
    sealed interface Kind {
        enum class Keyword : Kind {
            SAY, TO, STRING
        }

        data class Ident(val text: String): Kind //Identifiers
        data object EOL : Kind
        data object EOF : Kind
    }
}

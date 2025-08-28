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
            SAY, TO, STRING,
            PLACE, IN, SLOT,
            CRAFT, WITH, INTO,
            SHEAR, FROM,
            DISENCHANT, BY,
            SMITH,

            REDSTONE, THEN, ELSE, END, // if / else / end
            MINE, DO, // while
            SMELT, TIMES, // repeat N times
            TRAVEL, // for-range
            BEDROCK, TNT, // == and !=

            SACK, CONTAINS, HARVEST, AT // arrays
        }

        data class Ident(val text: String): Kind //Identifiers
        data class IntLit(val value: Long): Kind //Integer literals

        data object LBRACK: Kind
        data object RBRACK: Kind
        data object COMMA: Kind

        data object EOL : Kind
        data object EOF : Kind
    }
}

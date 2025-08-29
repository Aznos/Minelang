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
            SAY, ASK,
            PLACE, IN, TO, SLOT,
            CRAFT, WITH,
            SHEAR, FROM,
            DISENCHANT, BY,
            SMITH,

            REDSTONE, THEN, ELSE, END,
            MINE, DO,
            SMELT, TIMES,
            TRAVEL,
            BEDROCK, TNT,

            SACK, CONTAINS, HARVEST, AT, LENGTH,
            TRADE, SPRINT, SNEAK,

            BREW, AS,
            GRINDSTONE, FLOOR, CEIL, ROUND, TRUNC,
            CAULDRON, SCALE,

            INT_T, RAT_T, FLOAT_T, STRING_T,

            SLEEP
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

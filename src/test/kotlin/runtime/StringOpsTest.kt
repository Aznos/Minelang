package runtime

import support.runAndAssert
import kotlin.test.Test

class StringOpsTest {
    @Test fun scribeCopiesSack() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem]
                scribe slot 1 in slot 2
                say harvest slot 2 at 1
            """.trimIndent(),
            "104"
        )
    }

    @Test fun bindConcatsTwoSacks() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem, iron_bars]
                place sack in slot 2 contains [brick_stairs, brick_stairs, waterlily]
                bind slot 1 with slot 2 in slot 3
                length slot 3 in slot 4
                say slot 4
            """.trimIndent(),
            "5"
        )
    }

    @Test fun loomExtractsOneCharAsSack() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem, iron_bars, brick_stairs]
                loom slot 1 at 2 in slot 2
                say harvest slot 2 at 1
            """.trimIndent(),
            "101"
        )
    }
}
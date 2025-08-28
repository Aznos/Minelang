package runtime

import support.runAndAssert
import kotlin.test.Test

class ControlFlowTest {
    @Test fun redstoneElseMineTravelLengthHarvest_printsHello() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem, iron_bars, brick_stairs, brick_stairs, waterlily]
                length slot 1 in slot 2
                place stone in slot 4
                travel slot 3 from slot 4 to slot 2 do
                    say brew harvest slot 1 at slot 3 as string
                end
            """.trimIndent(),
            "hello"
        )
    }
}
package runtime

import support.runAndAssert
import kotlin.test.Test

class SackTest {
    @Test fun saySlotAsListAndAsString() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem, iron_bars]
                say slot 1
                length slot 1 in slot 2
                place stone in slot 3
                travel slot 4 from slot 3 to slot 2 do
                    say brew harvest slot 1 at slot 4 as string
                end
            """.trimIndent(),
            "104,101he"
        )
    }
}
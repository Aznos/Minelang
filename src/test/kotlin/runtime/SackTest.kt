package runtime

import support.runAndAssert
import kotlin.test.Test

class SackTest {
    @Test fun saySlotAsListAndAsString() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem, iron_bars]
                say slot 1
                say slot 1 to string
            """.trimIndent(),
            "104,101he"
        )
    }
}
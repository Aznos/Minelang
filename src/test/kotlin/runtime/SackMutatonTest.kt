package runtime

import support.runAndAssert
import kotlin.test.Test

class SackMutatonTest {
    @Test fun tradeByLiteralIndex_replacesElement() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem, iron_bars]
                trade slot 1 at 1 to diamond_ore
                say slot 1
                say slot 1 to string
            """.trimIndent(),
            "56,1018e"
        )
    }

    @Test fun tradeBySlotIndex_usesNumericFromSlot() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem, iron_bars]
                place stone in slot 9
                trade slot 1 at slot 9 to melon_block
                say slot 1
            """.trimIndent(),
            "103,101"
        )
    }

    @Test fun sprintAppendsToEnd() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem, iron_bars]
                sprint slot 1 with diamond_ore
                say slot 1
            """.trimIndent(),
            "104,101,56"
        )
    }

    @Test fun sneakRemovesElement() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem, iron_bars, diamond_ore]
                sneak slot 1
                say slot 1
            """.trimIndent(),
            "104,101"
        )
    }

    @Test fun mutableThenPrintChars_spellHe() {
        runAndAssert(
            """
                place sack in slot 1 contains [pumpkin_stem, pumpkin_stem]
                trade slot 1 at 2 to iron_bars
                say slot 1 to string
            """.trimIndent(),
            "he"
        )
    }
}
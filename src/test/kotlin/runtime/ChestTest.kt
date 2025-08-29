package runtime

import support.runAndAssert
import kotlin.test.Test

class ChestTest {
    @Test fun placeChestLiteralAndRaid() {
        runAndAssert(
            """
                place chest in slot 1 contains [key pumpkin_stem value iron_bars, key cobblestone value dirt]
                raid slot 1 key pumpkin_stem in slot 2
                say slot 2
            """.trimIndent(),
            "101"
        )
    }

    @Test fun stashUpdatesExistingAndRaid() {
        runAndAssert(
            """
                place chest in slot 1 contains [key pumpkin_stem value iron_bars]
                stash slot 1 key pumpkin_stem value brick_stairs
                raid slot 1 key pumpkin_stem in slot 2
                say slot 2
            """.trimIndent(),
            "108"
        )
    }

    @Test fun stashWithSlotKeyAndRaidWithItemKey() {
        runAndAssert(
            """
                place chest in slot 1 contains []
                place stone in slot 9
                stash slot 1 key slot 9 value melon_block
                raid slot 1 key stone in slot 3
                say slot 3
            """.trimIndent(),
            "103"
        )
    }
}
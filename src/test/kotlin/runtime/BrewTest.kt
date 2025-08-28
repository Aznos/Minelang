package runtime

import support.runAndAssert
import kotlin.test.Test

class BrewTest {
    @Test fun brewInsideSayToStringChar() {
        runAndAssert(
            """
                say brew cobblestone as string
            """.trimIndent(),
            "\\x04"
        )
    }

    @Test fun divideGivesRatThenBrewToFloat() {
        runAndAssert(
            """
                place cobblestone in slot 1
                place dirt in slot 2
                disenchant slot 1 by slot 2 in slot 3
                brew slot 3 as float in slot 4 with cauldron scale 4 with grindstone round
                say slot 4
            """.trimIndent(),
            "1.3333"
        )
    }

    @Test fun brewToIntWithFloor() {
        runAndAssert(
            """
                place cobblestone in slot 1
                place dirt in slot 2
                disenchant slot 1 by slot 2 in slot 3
                brew slot 3 as int in slot 5 with grindstone floor
                say slot 5
            """.trimIndent(),
            "1"
        )
    }

    @Test fun brewInsideSayFloatDirect() {
        runAndAssert(
            """
                place cobblestone in slot 1
                place dirt in slot 2
                disenchant slot 1 by slot 2 in slot 3
                say brew slot 3 as float with cauldron scale 3
            """.trimIndent(),
            "1.333"
        )
    }
}
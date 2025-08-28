package runtime

import support.runAndAssert
import kotlin.test.Test

class SmokeTest {
    @Test fun sayItemAsNumberAndChar() {
        runAndAssert(
            """
                say pumpkin_stem
                say pumpkin_stem to string
            """.trimIndent(),
            "104h"
        )
    }

    @Test fun addShearSmithDisenchant() {
        runAndAssert(
            """
                place cobblestone in slot 1
                place dirt in slot 2
                craft slot 1 with slot 2 in slot 3
                say slot 3
                shear slot 1 from slot 3 in slot 4
                say slot 4
                smith slot 1 with slot 2 in slot 5
                say slot 5
                place flowing_lava in slot 6
                disenchant slot 5 by slot 6 in slot 7
                say slot 7
            """.trimIndent(),
            "73121"
        )
    }
}
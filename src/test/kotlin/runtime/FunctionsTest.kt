package runtime

import support.runAndAssert
import kotlin.test.Test

class FunctionsTest {
    @Test fun simpleAddMulTwoReturns() {
        runAndAssert(
            """
                command addmul do
                    craft slot 1 with slot 2 in slot 3
                    smith slot 1 with slot 2 in slot 4
                
                    scribe slot 3 in slot 1
                    scribe slot 4 in slot 2
                end
                
                place stone in slot 9
                place cobblestone in slot 10
                activate addmul with [slot 9, slot 10] in slot [slot 5, slot 6]
                
                say slot 5
                say slot 6
            """.trimIndent(),
            "54"
        )
    }

    @Test fun concatStringsViaBindAndReturn() {
        runAndAssert(
            """
                command join3 do
                    bind slot 1 with slot 2 in slot 4
                    bind slot 4 with slot 3 in slot 5
                    scribe slot 5 in slot 1
                end
                
                place sack in slot 1 contains [pumpkin_stem, iron_bars]
                place sack in slot 2 contains [brick_stairs, brick_stairs]
                place sack in slot 3 contains [waterlily]
                
                activate join3 with [slot 1, slot 2, slot 3] in slot [slot 8]
                say brew slot 8 as string
            """.trimIndent(),
            "hello"
        )
    }
}
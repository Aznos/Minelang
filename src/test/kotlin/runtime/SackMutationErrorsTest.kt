package runtime

import io.Source
import lexer.Lexer
import parse.Parser
import runtime.core.Execution
import runtime.core.Machine
import runtime.core.RuntimeConfig
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SackMutationErrorsTest {
    private fun run(src: String) {
        val program = Parser(Lexer(Source.fromRaw("t", src)).lexAll()).parseProgram()
        val machine = Machine(RuntimeConfig(out = {}))
        Execution(program, machine).run()
    }

    @Test fun tradeIndexOutOfBounds() {
        assertFailsWith<IllegalArgumentException>() {
            run(
                """
                    place sack in slot 1 contains [pumpkin_stem]
                    trade slot 1 at 2 to diamond_ore
                """.trimIndent()
            )
        }
    }

    @Test fun sneakFromEmptySack() {
        assertFailsWith<IllegalArgumentException> {
            run(
                """
                    place sack in slot 1 contains []
                    sneak slot 1
                """.trimIndent()
            )
        }
    }

    @Test fun tradeOnNonSackSlot() {
        assertFailsWith<IllegalStateException> {
            run(
                """
                    place cobblestone in slot 1
                    trade slot 1 at 1 to diamond_ore
                """.trimIndent()
            )
        }
    }
}
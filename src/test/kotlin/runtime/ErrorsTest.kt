package runtime

import io.Source
import lexer.Lexer
import parse.Parser
import runtime.core.Execution
import runtime.core.Machine
import runtime.core.RuntimeConfig
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ErrorsTest {
    private fun run(src: String) {
        val program = Parser(Lexer(Source.fromRaw("t", src)).lexAll()).parseProgram()
        val machine = Machine(RuntimeConfig(out = {}))
        Execution(program, machine).run()
    }

    @Test fun usingSackWhereNumberExpected() {
        assertFailsWith<RuntimeException> {
            run(
                """
                    place sack in slot 1 contains [pumpkin_stem]
                    smith slot 1 with slot 1 into slot 3
                """.trimIndent()
            )
        }
    }

    @Test fun harvestIndexOutOfBounds() {
        assertFailsWith<IllegalArgumentException> {
            run(
                """
                    place sack in slot 1 contains [pumpkin_stem]
                    say harvest slot 1 at 2
                """.trimIndent()
            )
        }
    }
}
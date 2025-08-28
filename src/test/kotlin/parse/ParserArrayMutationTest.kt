package parse

import io.Source
import lexer.Lexer
import kotlin.test.Test

class ParserArrayMutationTest {
    private fun parseOk(src: String) = Parser(Lexer(Source.fromRaw("t", src)).lexAll()).parseProgram()

    @Test fun parsesTrade() {
        parseOk("trade slot 1 at 1 to diamond_ore")
        parseOk("""
            place stone in slot 9
            trade slot 1 at slot 9 to diamond_ore
        """.trimIndent())
    }

    @Test fun parsesSprint() {
        parseOk("sprint slot 1 with diamond_ore")
    }

    @Test fun parsesSneak() {
        parseOk("sneak slot 1")
    }
}
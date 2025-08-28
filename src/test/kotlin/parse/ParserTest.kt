package parse

import io.Source
import lexer.Lexer
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ParserTest {
    private fun parseOk(src: String) = Parser(Lexer(Source.fromRaw("t", src)).lexAll()).parseProgram()

    @Test fun parsePlaceSack() {
        parseOk("place sack in slot 1 contains [pumpkin, melon_block]")
    }

    @Test fun parseSayHarvest() {
        parseOk("say brew harvest slot 1 at 2 as string")
    }

    @Test fun redstoneRequiresThen() {
        assertFailsWith<ParseException> {
            parseOk("redstone slot 1 bedrock slot 2\n   say cobblestone\nend")
        }
    }
}
package lexer

import io.Source
import kotlin.test.Test
import kotlin.test.assertTrue

class LexerTest {
    private fun lex(txt: String) = Lexer(Source.fromRaw("t", txt)).lexAll()

    @Test fun bracketsAndCommasAroundSack() {
        val toks = lex("place sack in slot 1 contains [pumpkin, melon_block]")
        assertTrue(toks.any { it.kind is Token.Kind.LBRACK } )
        assertTrue(toks.any { it.kind is Token.Kind.COMMA } )
        assertTrue(toks.any { it.kind is Token.Kind.RBRACK } )
    }

    @Test fun harvestAtIndexFormsKeywords() {
        val toks = lex("say harvest slot 1 at 2")
        assertTrue(toks.any { (it.kind as? Token.Kind.Keyword) == Token.Kind.Keyword.HARVEST } )
        assertTrue(toks.any { (it.kind as? Token.Kind.Keyword) == Token.Kind.Keyword.AT } )
    }

    @Test fun intLiteralTokenized() {
        val toks = lex("slot 12")
        assertTrue(toks.any { (it.kind as? Token.Kind.IntLit)?.value == 12L })
    }
}
package support

import io.Source
import jdk.internal.org.jline.utils.Colors.s
import lexer.Lexer
import parse.Parser
import runtime.core.Execution
import runtime.core.Machine
import runtime.core.RuntimeConfig
import kotlin.test.assertEquals

/**
 * Runs a Minelang program from a raw string and returns the output as a string
 */
fun runProgram(sourceText: String): String {
    val sink = StringBuilder()
    val cfg = RuntimeConfig(out = { s -> sink.append(s) })
    val machine = Machine(cfg)

    val program = Parser(Lexer(Source.fromRaw("test", sourceText)).lexAll()).parseProgram()
    Execution(program, machine).run()

    return sink.toString()
}

/**
 * Assert that running [sourceText] prints exactly [expected]
 */
fun runAndAssert(sourceText: String, expected: String) {
    val out = runProgram(sourceText)
    assertEquals(expected, out, "Output mismatch")
}
import io.Source
import io.SourceLoader
import lexer.Lexer
import parse.ParseException
import parse.Parser
import runtime.core.Execution
import runtime.core.Machine
import runtime.core.RuntimeConfig

/**
 * Main entry point
 */
fun main(args: Array<String>) {
    val source: Source = if(args.isNotEmpty()) {
        SourceLoader.fromFile(args[0])
    } else {
        println("Reading program from stdin. Press Ctrl+D (Unix) or Ctrl+Z (Windows) to end input.")
        SourceLoader.fromStdin()
    }

    println("Loaded: ${source.origin}")
    println("Line count: ${source.lineCount}")
    println("---------- Program Start ----------")

    try {
        val tokens = Lexer(source).lexAll()
        val program = Parser(tokens).parseProgram()
        val machine = Machine(RuntimeConfig())
        Execution(program, machine).run()
    } catch(e: ParseException) {
        System.err.println("Parse error in ${source.origin}: ${e.message}")
        kotlin.system.exitProcess(1)
    } catch(e: Exception) {
        System.err.println("Error: ${e.message}")
        kotlin.system.exitProcess(2)
    }
}
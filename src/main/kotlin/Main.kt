import io.Source
import io.SourceLoader
import lexer.Lexer

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

    val tokens = Lexer(source).lexAll()
    for(token in tokens) {
        println(token)
    }
}
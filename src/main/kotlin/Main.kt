import io.Source
import io.SourceLoader

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

    source.lines().forEachIndexed { idx, line ->
        val n = idx + 1 // Line numbers start at 1 for user display
        println(String.format("%4d | %s", n, line))
    }
}
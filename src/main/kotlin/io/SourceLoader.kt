package io

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Utilities for loading program text into [Source] objects
 *
 * This is the only place that should touch the filesystem or other I/O sources
 */
object SourceLoader {
    private val UTF8: Charset = StandardCharsets.UTF_8

    /**
     * Loads a source file from [pathStr]
     *
     * @param pathStr Filesystem path to the program
     * @return A [Source] object containing the program text and origin information
     *
     * @throws IllegalArgumentException if the file does not exist or is a directory
     * @throws RuntimeException if the file cannot be read for any reason
     */
    fun fromFile(pathStr: String): Source {
        val path = Path.of(pathStr).toAbsolutePath().normalize()
        require(Files.exists(path)) { "File does not exist: $path" }
        require(!Files.isDirectory(path)) { "Path is a directory, not a file: $path" }

        val bytes =try {
            Files.readAllBytes(path)
        } catch(e: Exception) {
            throw RuntimeException("Failed to read file: $path", e)
        }

        val text = bytes.toString(UTF8)
        return Source.fromRaw(path.toString(), text)
    }

    /**
     * Reads all remaining text from standard input, producing a [Source] object
     *
     * @param originLabel Label to use for the origin
     * @return A [Source] object containing the program text and origin information
     */
    fun fromStdin(originLabel: String = "stdin"): Source {
        val raw = generateSequence(::readlnOrNull).joinToString("\n")
        return Source.fromRaw(originLabel, raw)
    }

    fun fromStdinUntilDelimiter(originLabel: String = "stdin", delimiter: String = "%%"): Source {
        val br = BufferedReader(InputStreamReader(System.`in`))
        val lines = mutableListOf<String>()
        while(true) {
            val line = br.readLine() ?: break
            if(line == delimiter) break
            lines += line
        }

        val raw = lines.joinToString("\n")
        return Source.fromRaw(originLabel, raw)
    }
}
package io

/**
 * Container for the raw program text plus origin information
 *
 * @property origin A human-readable label from where the source was loaded (e.g. "/path/to/file.mc" or "stdin")
 * @property content The entire program text
 */
data class Source(
    val origin: String,
    val content: String
) {
    //Returns the number of lines in the source (1 if empty)
    val lineCount: Int
        get() = if(content.isEmpty()) 1 else content.count { it == '\n' } + 1

    /**
     * Returns the zero-based line at [index]
     *
     * @throws IndexOutOfBoundsException if [index] is not in [0, lineCount]
     */
    fun line(index: Int): String {
        require(index in 0 until lineCount) { "Line index out of bounds: $index (0..${lineCount - 1}" }
        return content.lineSequence().elementAt(index)
    }

    /**
     * Returns all lines in the source as a list
     */
    fun lines(): List<String> = content.split('\n')

    companion object {
        /**
         * Creates a [io.Source] from raw text, applying newline normalization and removing BOM if present
         *
         * @param origin A human-readable label from where the source was loaded (e.g. "/path/to/file.mc" or "stdin")
         * @param raw The entire program text as read from the source
         */
        fun fromRaw(origin: String, raw: String): Source {
            val normalized = raw
                .removePrefix("\uFEFF") //Remove BOM if present
                .replace("\r\n", "\n") //Windows CRLF -> LF
                .replace("\r", "\n") //Old Mac CR -> LF

            return Source(origin, normalized)
        }
    }
}
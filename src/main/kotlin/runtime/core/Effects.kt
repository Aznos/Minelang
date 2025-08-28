package runtime.core

/**
 * Centralized output rendering
 */
object Effects {
    fun renderAscii(v: Long, cfg: RuntimeConfig): String {
        val ascii = v.toInt()
        return when(ascii) {
            0 -> if (cfg.asciiNulAsSpace) " " else "\\x00"
            10, 13 -> if (cfg.asciiCrLfAsNewLine) "\n" else "\\x%02X".format(ascii)
            in 32..126 -> ascii.toChar().toString()
            in 0..255 -> "\\x%02X".format(ascii)
            else -> "\\u%04X".format(ascii)
        }
    }
}
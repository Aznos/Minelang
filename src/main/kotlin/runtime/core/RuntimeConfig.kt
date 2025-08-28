package runtime.core

/**
 * Configuration for the runtime environment
 */
data class RuntimeConfig(
    val slots: Int = 36,
    val out: (String) -> Unit = { print(it) },
    val asciiNulAsSpace: Boolean = true,
    val asciiCrLfAsNewLine: Boolean = true
)

package runtime.core

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Configuration for the runtime environment
 */
data class RuntimeConfig(
    val slots: Int = 36,
    val out: (String) -> Unit = { print(it) },
    val asciiNulAsSpace: Boolean = true,
    val asciiCrLfAsNewLine: Boolean = true,
    val inReader: BufferedReader = BufferedReader(InputStreamReader(System.`in`)),
    val sleepFn: (Long) -> Unit = { Thread.sleep(it) }
)

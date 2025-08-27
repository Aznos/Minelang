package runtime

import parse.Instr
import parse.Program

/**
 * Executes a [parse.Program]
 */
class VM(
    private val program: Program,
    private val out: (String) -> Unit = { print(it) }
) {
    /**
     * Runs the program to completion
     */
    fun run() {
        for(ins in program.instructions) {
            when(ins) {
                is Instr.Say -> execSay(ins)
            }
        }
    }

    private fun execSay(s: Instr.Say) {
        val id = ItemRegistry.idOf(s.itemName)
            ?: error("Unknown item '${s.itemName}'")

        if(!s.toString) {
            out(id.toString())
        } else {
            val ascii = id
            val rendered = when (ascii) {
                in 32..126 -> ascii.toChar().toString()
                in 0..255 -> "\\x%02X".format(ascii)
                else -> "\\u%04X".format(ascii)
            }
            out(rendered)
        }
    }
}
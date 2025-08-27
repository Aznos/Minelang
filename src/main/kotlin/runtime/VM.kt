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
    private val slots = LongArray(37) //Index 0 is unused

    /**
     * Runs the program to completion
     */
    fun run() {
        for(ins in program.instructions) {
            when(ins) {
                is Instr.Place -> execPlace(ins)
                is Instr.SayItem -> execSayItem(ins)
                is Instr.SaySlot -> execSaySlot(ins)
                is Instr.CraftAdd -> execCraftAdd(ins)
            }
        }
    }

    private fun checkSlot(n: Int) {
        require(n in 1..36) { "Slot number must be between 1 and 36, got $n" }
    }

    private fun execPlace(p: Instr.Place) {
        val id = ItemRegistry.idOf(p.itemName) ?: error("Unknown item '${p.itemName}'")
        checkSlot(p.slot)
        slots[p.slot] = id.toLong()
    }

    private fun execSayItem(s: Instr.SayItem) {
        val id = ItemRegistry.idOf(s.itemName) ?: error("Unknown item '${s.itemName}'")
        sayValue(id.toLong(), s.toString)
    }

    private fun execSaySlot(s: Instr.SaySlot) {
        checkSlot(s.slot)
        sayValue(slots[s.slot], s.toString)
    }

    private fun execCraftAdd(c: Instr.CraftAdd) {
        checkSlot(c.a); checkSlot(c.b); checkSlot(c.c)
        slots[c.c] = slots[c.a] + slots[c.b]
    }

    private fun sayValue(v: Long, asChar: Boolean) {
        if(!asChar) {
            out(v.toString())
        } else {
            val ascii = v.toInt()
            val rendered = when (ascii) {
                0 -> " "
                10, 13 -> "\n"
                in 32..126 -> ascii.toChar().toString()
                in 0..255 -> "\\x%02X".format(ascii)
                else -> "\\u%04X".format(ascii)
            }

            out(rendered)
        }
    }
}
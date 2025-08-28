package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Say {
    fun handleSayItem(m: Machine, s: Instr.SayItem) {
        val id = ItemRegistry.idOf(s.itemName) ?: error("Unknown item: ${s.itemName}")
        if(!s.toString) m.emitNumber(id.toLong()) else m.emitAscii(id.toLong())
    }

    fun handleSaySlot(m: Machine, s: Instr.SaySlot) {
        val v = m.get(s.slot)
        if(!s.toString) m.emitNumber(v) else m.emitAscii(v)
    }
}
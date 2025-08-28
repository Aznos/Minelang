package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.core.Value
import runtime.registry.ItemRegistry
import kotlin.collections.toIntArray

object Say {
    fun handleSayItem(m: Machine, s: Instr.SayItem) {
        val id = ItemRegistry.idOf(s.itemName) ?: error("Unknown item: ${s.itemName}")
        if(!s.toString) m.emitNumber(id.toLong()) else m.emitAscii(id.toLong())
    }

    fun handleSaySlot(m: Machine, s: Instr.SaySlot) {
        when(val v = m.getRaw(s.slot)) {
            is Value.Num -> if(!s.toString) m.emitAscii(v.v) else m.emitAscii(v.v)
            is Value.Sack ->
                if(s.toString) {
                    m.emitAsciiMany(v.items)
                } else {
                    m.emitList(v.items)
                }
        }
    }
}
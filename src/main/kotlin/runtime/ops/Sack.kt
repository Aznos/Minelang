package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.core.Value
import runtime.registry.ItemRegistry

object Sack {
    fun handlePlaceSack(m: Machine, i: Instr.PlaceSack) {
        val ids = i.items.map { name ->
            ItemRegistry.idOf(name) ?: error("Unknown item: '$name' in sack")
        }.toIntArray()

        m.setSack(i.slot, ids)
    }

    fun handleLength(m: Machine, i: Instr.Length) {
        val v = m.getRaw(i.sackSlot)
        val sack = v as? Value.Sack ?: error("Slot ${i.sackSlot} does not contain a sack")
        m.setNum(i.dst, sack.items.size.toLong())
    }
}
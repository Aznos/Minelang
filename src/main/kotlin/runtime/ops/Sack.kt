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

    fun handleTrade(m: Machine, i: Instr.Trade) {
        val sack = m.requireSack(i.sackSlot)
        val idx = m.evalIndex(i.index)
        require(idx in 1..sack.size) {
            "Index $idx out of bounds for sack of size ${sack.size}"
        }

        val id = ItemRegistry.idOf(i.itemName) ?: error("Unknown item: '${i.itemName}'")
        sack[idx - 1] = id
        m.setSack(i.sackSlot, sack)
    }

    fun handleSprint(m: Machine, i: Instr.Sprint) {
        val sack = m.requireSack(i.sackSlot)
        val id = ItemRegistry.idOf(i.itemName) ?: error("Unknown item: '${i.itemName}'")
        val grown = sack.copyOf(sack.size + 1)

        grown[grown.lastIndex] = id
        m.setSack(i.sackSlot, grown)
    }

    fun handleSneak(m: Machine, i: Instr.Sneak) {
        val sack = m.requireSack(i.sackSlot)
        require(sack.isNotEmpty()) { "Cannot sneak (pop) from an empty sack" }
        m.setSack(i.sackSlot, sack.copyOf(sack.size - 1))
    }
}
package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Move {
    fun handlePlace(m: Machine, p: Instr.Place) {
        val id = ItemRegistry.idOf(p.itemName) ?: error("Unknown item: ${p.itemName}")
        m.setNum(p.slot, id.toLong())
    }
}
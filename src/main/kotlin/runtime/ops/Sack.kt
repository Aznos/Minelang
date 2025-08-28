package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Sack {
    fun handlePlaceSack(m: Machine, i: Instr.PlaceSack) {
        val ids = i.items.map { name ->
            ItemRegistry.idOf(name) ?: error("Unknown item: '$name' in sack")
        }.toIntArray()

        m.setSack(i.slot, ids)
    }
}
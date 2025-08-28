package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Add {
    fun handleCraft(m: Machine, c: Instr.CraftAdd) {
        val res = m.get(c.a) + m.get(c.b)
        m.set(c.c, res)
    }
}
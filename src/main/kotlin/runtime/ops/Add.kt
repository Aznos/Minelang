package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Add {
    fun handleCraft(m: Machine, c: Instr.CraftAdd) {
        val res = m.getNum(c.a) + m.getNum(c.b)
        m.setNum(c.c, res)
    }
}
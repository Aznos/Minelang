package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Sub {
    fun handleShear(m: Machine, c: Instr.ShearSub) {
        val res = m.get(c.b) - m.get(c.a)
        m.set(c.c, res)
    }
}
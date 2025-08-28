package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Sub {
    fun handleShear(m: Machine, c: Instr.ShearSub) {
        val res = m.getNum(c.b) - m.getNum(c.a)
        m.setNum(c.c, res)
    }
}
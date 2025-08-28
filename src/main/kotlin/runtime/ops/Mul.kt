package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Mul {
    fun handleSmith(m: Machine, c: Instr.SmithMul) {
        val res = m.getNum(c.a) * m.getNum(c.b)
        m.setNum(c.c, res)
    }
}
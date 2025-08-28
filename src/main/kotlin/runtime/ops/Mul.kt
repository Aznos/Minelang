package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Mul {
    fun handleSmith(m: Machine, c: Instr.SmithMul) {
        val res = m.get(c.a) * m.get(c.b)
        m.set(c.c, res)
    }
}
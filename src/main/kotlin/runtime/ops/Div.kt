package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Div {
    fun handleDisenchant(m: Machine, c: Instr.DisenchantDiv) {
        val denom = m.get(c.b)
        require(denom != 0L) { "Division by zero in slot ${c.b}" }
        val res = m.get(c.a) / denom

        m.set(c.c, res)
    }
}
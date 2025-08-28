package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.core.Value
import runtime.registry.ItemRegistry

object Div {
    fun handleDisenchant(m: Machine, i: Instr.DisenchantDiv) {
        val a = m.getNum(i.a)
        val b = m.getNum(i.b)
        require(b != 0L) { "Division by zero in slot ${i.b}" }

        val q = a / b
        val r = a % b
        if(r == 0L) {
            m.setNum(i.c, q)
        } else {
            m.setValue(i.c, Value.Rat(a, b))
        }
    }
}
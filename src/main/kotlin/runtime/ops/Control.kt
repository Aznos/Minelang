package runtime.ops

import parse.Cmp
import parse.Condition
import parse.Instr
import parse.Operand
import runtime.core.Machine
import runtime.registry.ItemRegistry

object Control {
    private fun evalOperand(m: Machine, op: Operand): Long = when(op) {
        is Operand.Slot -> m.get(op.n)
        is Operand.Item -> ItemRegistry.idOf(op.name)?.toLong() ?: error("Unknown item: ${op.name}")
        is Operand.Number -> op.value.toLong()
    }

    private fun test(m: Machine, cond: Condition): Boolean {
        val l = evalOperand(m, cond.left)
        val r = evalOperand(m, cond.right)
        return when(cond.cmp) {
            Cmp.BEDROCK_EQ -> l == r
            Cmp.TNT_NE -> l != r
        }
    }

    fun handleRedstone(exec: (List<Instr>) -> Unit, m: Machine, i: Instr.Redstone) {
        if(test(m, i.cond)) exec(i.thenBlock) else exec(i.elseBlock ?: emptyList())
    }

    fun handleMine(exec: (List<Instr>) -> Unit, m: Machine, i: Instr.Mine) {
        while(test(m, i.cond)) exec(i.body)
    }

    fun handleSmelt(exec: (List<Instr>) -> Unit, m: Machine, i: Instr.Smelt) {
        val times = m.get(i.countSlot)
        if(times <= 0) return
        repeat(times.toInt()) {
            exec(i.body)
        }
    }

    fun handleTravel(exec: (List<Instr>) -> Unit, m: Machine, i: Instr.Travel) {
        val cur = m.get(i.startSlot)
        val end = m.get(i.endSlot)
        m.set(i.indexSlot, cur)

        if(cur <= end) {
            while(m.get(i.indexSlot) <= end) {
                exec(i.body)
                m.set(i.indexSlot, m.get(i.indexSlot) + 1)
            }
        } else {
            while(m.get(i.indexSlot) >= end) {
                exec(i.body)
                m.set(i.indexSlot, m.get(i.indexSlot) - 1)
            }
        }
    }
}
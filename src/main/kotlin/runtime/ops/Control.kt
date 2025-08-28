package runtime.ops

import parse.Cmp
import parse.Condition
import parse.Instr
import parse.Operand
import runtime.core.Machine
import runtime.core.Value
import runtime.registry.ItemRegistry

object Control {
    private fun evalOperand(m: Machine, op: Operand): Long = when(op) {
        is Operand.Slot -> m.getNum(op.n)
        is Operand.Item -> ItemRegistry.idOf(op.name)?.toLong() ?: error("Unknown item: ${op.name}")
        is Operand.Number -> op.value.toLong()
        is Operand.Harvest -> {
            val sackVal = m.getRaw(op.sackSlot)
            val sack = sackVal as? Value.Sack ?: error("Slot ${op.sackSlot} does not contain a sack")
            val idx = evalIndex(m, op.index)
            require(idx in 1..sack.items.size) {
                "Index $idx out of bounds for sack in slot ${op.sackSlot} (size ${sack.items.size})"
            }

            sack.items[idx - 1].toLong()
        }
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
        val times = m.getNum(i.countSlot)
        if(times <= 0) return
        repeat(times.toInt()) {
            exec(i.body)
        }
    }

    fun handleTravel(exec: (List<Instr>) -> Unit, m: Machine, i: Instr.Travel) {
        val cur = m.getNum(i.startSlot)
        val end = m.getNum(i.endSlot)
        m.setNum(i.indexSlot, cur)

        if(cur <= end) {
            while(m.getNum(i.indexSlot) <= end) {
                exec(i.body)
                m.setNum(i.indexSlot, m.getNum(i.indexSlot) + 1)
            }
        } else {
            while(m.getNum(i.indexSlot) >= end) {
                exec(i.body)
                m.setNum(i.indexSlot, m.getNum(i.indexSlot) - 1)
            }
        }
    }

    private fun evalIndex(m: Machine, idxOp: Operand): Int {
        val v = when(idxOp) {
            is Operand.Number -> idxOp.value
            is Operand.Slot -> m.getNum(idxOp.n)
            else -> error("Index operand must be a number or slot")
        }

        return v.toInt()
    }
}
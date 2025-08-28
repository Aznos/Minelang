package runtime.ops

import parse.Instr
import parse.Operand
import runtime.core.Machine
import runtime.core.Value
import runtime.registry.ItemRegistry
import kotlin.collections.toIntArray

object Say {
    fun handleSayExpr(m: Machine, s: Instr.SayExpr) {
        when(val op = s.operand) {
            is Operand.Slot -> {
                when(val v = m.getRaw(op.n)) {
                    is Value.Num -> if(!s.toString) m.emitNumber(v.v) else m.emitAscii(v.v)
                    is Value.Sack -> if(s.toString) m.emitAsciiMany(v.items) else m.emitList(v.items)
                }
            }
            else -> {
                val v = evalOperand(m, op)
                if(!s.toString) m.emitNumber(v) else m.emitAscii(v)
            }
        }
    }

    private fun evalOperand(m: Machine, op: Operand): Long = when(op) {
        is Operand.Slot -> m.getNum(op.n)
        is Operand.Item -> ItemRegistry.idOf(op.name)?.toLong() ?: error("Unknown item: ${op.name}")
        is Operand.Number -> op.value.toLong()
        is Operand.Harvest -> {
            val sackVal = m.getRaw(op.sackSlot)
            val sack = sackVal as? Value.Sack ?: error("Slot ${op.sackSlot} does not contain a sack")
            val idx = when(op.index) {
                is Operand.Number -> op.index.value
                is Operand.Slot -> m.getNum(op.index.n).toInt()
                else -> error("Invalid index operand for Harvest: ${op.index}")
            }

            require(idx in 1..sack.items.size) {
                "Index $idx out of bounds for sack in slot ${op.sackSlot} (size ${sack.items.size})"
            }

            sack.items[idx - 1].toLong()
        }
    }
}
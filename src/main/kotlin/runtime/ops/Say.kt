package runtime.ops

import parse.Instr
import parse.Operand
import runtime.core.Machine
import runtime.core.Value
import runtime.registry.ItemRegistry

object Say {
    fun handleSayExpr(m: Machine, s: Instr.SayExpr) {
        val v = Eval.evalOperand(m, s.operand)
        m.emitValue(v)
    }
}

object Eval {
    fun evalOperand(m: Machine, op: Operand): Value = when(op) {
        is Operand.Slot -> m.getRaw(op.n)
        is Operand.Item -> {
            val id = ItemRegistry.idOf(op.name) ?: error("Unknown item: ${op.name}")
            Value.Num(id.toLong())
        }
        is Operand.Number -> Value.Num(op.value.toLong())
        is Operand.Harvest -> {
            val sackVal = m.getRaw(op.sackSlot)
            val sack = sackVal as? Value.Sack ?: error("Slot ${op.sackSlot} does not contain a sack")
            val idx = when(op.index) {
                is Operand.Number -> op.index.value
                is Operand.Slot -> m.getNum(op.index.n).toInt()
                else -> error("Invalid index operand: ${op.index}")
            }

            require(idx in 1..sack.items.size) { "Index $idx out of bounds for sack in slot ${op.sackSlot} (size ${sack.items.size})" }
            Value.Num(sack.items[idx - 1].toLong())
        }
        is Operand.Brew -> BrewExec.evalBrew(m, op)
        is Operand.SackLiteral -> {
            val ids = op.items.map { name ->
                ItemRegistry.idOf(name) ?: error("Unknown item: $name")
            }.toIntArray()
            Value.Sack(ids)
        }
    }
}
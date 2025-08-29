package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.core.Value

object Functions {
    fun handleCommand(m: Machine, c: Instr.Command) {
        m.commands[c.name.lowercase()] = c.body
    }

    fun handleActivate(runBlock: (List<Instr>) -> Unit, m: Machine, a: Instr.Activate) {
        val body = m.commands[a.name.lowercase()] ?: error("Unknown command: ${a.name}")
        val argVals = a.args.map { Eval.evalOperand(m, it) }
        val snapshot: Array<Value?> = Array(m.config.slots + 1) { null }
        for(i in 1..m.config.slots) {
            snapshot[i] = copyValue(m.getRaw(i))
        }

        for(i in 1..m.config.slots) m.setValue(i, Value.Num(0))
        argVals.forEachIndexed { idx, v -> m.setValue(idx + 1, copyValue(v)) }

        runBlock(body)

        val r = a.dstSlots.size
        val retVals = (1..r).map { copyValue(m.getRaw(it)) }
        for(i in 1..m.config.slots) {
            val v = snapshot[i]
            if(v == null) {
                //empty
            } else {
                m.setValue(i, v)
            }
        }

        a.dstSlots.forEachIndexed { idx, slot ->
            m.setValue(slot, retVals[idx])
        }
    }

    private fun copyValue(v: Value): Value = when(v) {
        is Value.Num -> Value.Num(v.v)
        is Value.Rat -> Value.Rat(v.num, v.den)
        is Value.FloatStr -> Value.FloatStr(v.text)
        is Value.CharCode -> Value.CharCode(v.code)
        is Value.Sack -> Value.Sack(v.items.copyOf())
        is Value.Chest -> Value.Chest(v.map.toMutableMap())
    }
}
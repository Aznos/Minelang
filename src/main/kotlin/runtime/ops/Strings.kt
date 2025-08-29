package runtime.ops

import parse.Instr
import parse.Operand
import runtime.core.Machine
import runtime.core.Value
import java.awt.SystemTray

object Strings {
    fun handleScribe(m: Machine, i: Instr.Scribe) {
        val v = m.getRaw(i.src)
        val copy = when(v) {
            is Value.Sack -> Value.Sack(v.items.copyOf())
            is Value.Chest -> Value.Chest(v.map.toMutableMap())
            is Value.Rat -> Value.Rat(v.num, v.den)
            is Value.Num -> Value.Num(v.v)
            is Value.FloatStr -> Value.FloatStr(v.text)
            is Value.CharCode -> Value.CharCode(v.code)
        }

        m.setValue(i.dst, copy)
    }

    fun handleBind(m: Machine, i: Instr.Bind) {
        val a = m.getRaw(i.a) as? Value.Sack ?: error("Slot ${i.a} is not a sack")
        val b = m.getRaw(i.b) as? Value.Sack ?: error("Slot ${i.b} is not a sack")
        val out = IntArray(a.items.size + b.items.size)

        System.arraycopy(a.items, 0, out, 0, a.items.size)
        System.arraycopy(b.items, 0, out, a.items.size, b.items.size)
        m.setSack(i.dst, out)
    }

    fun handleLoom(m: Machine, i: Instr.Loom) {
        val arr = m.requireSack(i.sackSlot)
        val idx = when(i.index) {
            is Operand.Number -> i.index.value
            is Operand.Slot -> m.getNum(i.index.n).toInt()
            else -> error("Invalid index operand ${i.index}")
        }

        require(idx in 1..arr.size) { "Index $idx out of bounds for sack of size ${arr.size}" }
        m.setSack(i.dst, intArrayOf(arr[idx - 1]))
    }

    fun handleFlip(m: Machine, i: Instr.Flip) {
        val arr = m.requireSack(i.sackSlot)
        require(arr.isNotEmpty()) { "Cannot flip from empty sack in slot ${i.sackSlot}" }

        val out = IntArray(arr.size)
        for(j in arr.indices) {
            out[j] = arr[arr.size - 1 - j]
        }

        m.setSack(i.dst, out)
    }
}
package runtime.ops

import parse.Cmp
import parse.Condition
import parse.Instr
import parse.Operand
import runtime.core.Machine
import runtime.core.Value
import runtime.ops.Eval.evalOperand
import runtime.registry.ItemRegistry
import kotlin.math.abs

object Control {
    private fun evalValue(m: Machine, op: Operand): Value =
        Eval.evalOperand(m, op)

    private fun valuesEqual(a: Value, b: Value): Boolean {
        return when {
            a is Value.Sack || b is Value.Sack ->
                error("Cannot compare sacks directly; brew or harvest into a comparable value")

            a is Value.CharCode && b is Value.CharCode -> a.code == b.code
            a is Value.Rat && b is Value.Rat -> a.num * b.den == b.num * a.den
            a is Value.Rat && b is Value.Num -> a.num == b.v * a.den
            a is Value.Num && b is Value.Rat -> b.num == a.v * b.den
            a is Value.Num && b is Value.Num -> a.v == b.v

            else -> {
                val da = toDouble(a)
                val db = toDouble(b)
                equalWithEpsilon(da, db)
            }
        }
    }

    private fun toDouble(v: Value): Double = when (v) {
        is Value.Num -> v.v.toDouble()
        is Value.Rat -> if(v.den == 0L) Double.NaN else v.num.toDouble() / v.den.toDouble()
        is Value.FloatStr -> v.text.toDoubleOrNull() ?: Double.NaN
        is Value.CharCode -> v.code.toDouble()
        is Value.Sack -> Double.NaN
        is Value.Chest -> Double.NaN
    }

    private fun equalWithEpsilon(a: Double, b: Double, eps: Double = 1e-9): Boolean {
        if(a.isNaN() || b.isNaN()) return false
        return abs(a - b) <= eps
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
}
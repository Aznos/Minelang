package runtime.ops

import parse.BrewType
import parse.Operand
import parse.Rounding
import runtime.core.Effects
import runtime.core.Machine
import runtime.core.Value
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round

object BrewExec {
    fun evalBrew(m: Machine, b: Operand.Brew): Value {
        val base = Eval.evalOperand(m, b.value)

        return when(b.target) {
            BrewType.RAT -> asRat(base)
            BrewType.INT -> Value.Num(applyRounding(toDouble(base), b.rounding ?: Rounding.TRUNC).toLong())
            BrewType.FLOAT -> {
                val sc = b.scale ?: 4
                val d = toDouble(base)
                Value.FloatStr(formatWithScale(d, sc, b.rounding ?: Rounding.ROUND))
            }
            BrewType.STRING -> asStringValue(m, base)
        }
    }

    private fun asStringValue(m: Machine, v: Value): Value {
        val text = when(v) {
            is Value.Num -> Effects.renderAscii(v.v, m.config)
            is Value.Rat -> "${v.num}/${v.den}"
            is Value.FloatStr -> v.text
            is Value.CharCode -> Effects.renderAscii(v.code, m.config)
            is Value.Sack -> buildString {
                for(id in v.items) append(Effects.renderAscii(id.toLong(), m.config))
            }
            is Value.Chest -> buildString {
                for((k, v) in v.map) {
                    append(Effects.renderAscii(k.toLong(), m.config))
                    append('=')
                    append(Effects.renderAscii(v.toLong(), m.config))
                }
            }
        }
        return Value.FloatStr(text)
    }

    fun handleBrewInto(m: Machine, value: Operand, target: BrewType, dst: Int, rounding: Rounding?, scale: Int?) {
        val brewed = evalBrew(m, Operand.Brew(value, target, rounding, scale))
        m.setValue(dst, brewed)
    }

    private fun toDouble(v: Value): Double = when(v) {
        is Value.Num -> v.v.toDouble()
        is Value.Rat -> v.num.toDouble() / v.den.toDouble()
        is Value.FloatStr -> v.text.toDoubleOrNull() ?: 0.0
        is Value.CharCode -> v.code.toDouble()
        is Value.Sack -> {
            var sum = 0L
            for(id in v.items) sum += id.toLong()
            sum.toDouble()
        }
        is Value.Chest -> 0.0
    }

    private fun asRat(v: Value): Value.Rat = when(v) {
        is Value.Rat -> v
        is Value.Num -> Value.Rat(v.v, 1)
        is Value.FloatStr -> {
            val d = v.text.toDoubleOrNull() ?: 0.0
            toRat(d, 1_000_000)
        }
        is Value.CharCode -> Value.Rat(v.code, 1)
        is Value.Sack -> Value.Rat(0, 1)
        is Value.Chest -> Value.Rat(0, 1)
    }

    private fun applyRounding(d: Double, rule: Rounding): Double = when(rule) {
        Rounding.FLOOR -> floor(d)
        Rounding.CEIL -> ceil(d)
        Rounding.ROUND -> round(d)
        Rounding.TRUNC -> if(d >= 0) floor(d) else ceil(d)
    }

    private fun formatWithScale(d: Double, scale: Int, rule: Rounding): String {
        val factor = 10.0.pow(scale.toDouble())
        val r = applyRounding(d * factor, rule) / factor
        return "%.${scale}f".format(r)
    }

    private fun toRat(d: Double, maxDen: Int): Value.Rat {
        var x = d
        var num0 = 0L; var den0 = 1L
        var num1 = 1L; var den1 = 0L
        var n = 0

        while(n < 20) {
            val a = floor(x).toLong()
            val num2 = a * num1 + num0
            val den2 = a * den1 + den0
            if(den2 > maxDen) break
            if(x == a.toDouble()) {
                return Value.Rat(num2, den2)
            }

            num0 = num1
            den0 = den1
            num1 = num2
            den1 = den2

            x = 1.0 / (x - a)
            n++
        }

        return Value.Rat((d * maxDen).toLong(), maxDen.toLong())
    }
}
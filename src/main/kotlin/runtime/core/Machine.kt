package runtime.core

/**
 * Holds machine state and exposes helpers for ops to use
 */
class Machine(
    val config: RuntimeConfig
) {
    private val slots: Array<Value?> = arrayOfNulls(config.slots + 1)

    fun checkSlot(n: Int) {
        require(n in 1..config.slots) { "Slot $n is out of bounds (1..${config.slots})" }
    }

    fun requireSack(n: Int): IntArray {
        val v = getRaw(n)
        val sack = v as? Value.Sack ?: error("Slot $n does not contain a sack")
        return sack.items.copyOf()
    }

    fun evalIndex(idxOp: parse.Operand): Int {
        val v = when(idxOp) {
            is parse.Operand.Number -> idxOp.value
            is parse.Operand.Slot -> getNum(idxOp.n)
            else -> error("Invalid index operand: $idxOp")
        }

        return v.toInt()
    }

    fun setValue(n: Int, v: Value) {
        checkSlot(n)
        slots[n] = v
    }

    fun setNum(n: Int, v: Long) {
        checkSlot(n)
        slots[n] = Value.Num(v)
    }

    fun setSack(n: Int, items: IntArray) {
        checkSlot(n)
        slots[n] = Value.Sack(items.copyOf())
    }

    fun getRaw(n: Int): Value {
        checkSlot(n)
        return slots[n] ?: Value.Num(0)
    }

    fun getNum(n: Int): Long {
        return when(val v = getRaw(n)) {
            is Value.Num -> v.v
            is Value.Rat -> if(v.den != 0L && v.num % v.den == 0L) v.num / v.den else throw RuntimeException("Slot $n contains a non-integer rational number ${v.num}/${v.den}")
            is Value.FloatStr -> throw RuntimeException("Slot $n contains a non-integer float string '${v.text}'")
            is Value.CharCode -> v.code
            is Value.Sack -> throw RuntimeException("Slot $n contains a sack, not a number")
        }
    }

    fun emit(s: String) = config.out(s)
    fun emitValue(v: Value) {
        when(v) {
            is Value.Num -> emit(v.v.toString())
            is Value.Rat -> emit("${v.num}/${v.den}")
            is Value.FloatStr -> emit(v.text)
            is Value.CharCode -> emit(Effects.renderAscii(v.code, config))
            is Value.Sack -> emit(v.items.joinToString(","))
        }
    }

    fun readLineOrNull(): String? = config.inReader.readLine()
    fun flush() { System.out.flush() }
}
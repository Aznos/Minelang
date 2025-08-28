package runtime.core

sealed interface Value {
    data class Num(val v: Long) : Value
    data class Sack(val items: IntArray) : Value
}

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
            is Value.Sack -> throw RuntimeException("Slot $n contains a sack, not a number")
        }
    }

    fun emitNumber(v: Long) = config.out(v.toString())
    fun emitAscii(v: Long) = config.out(Effects.renderAscii(v, config))
    fun emitAsciiMany(values: IntArray) {
        for(x in values) {
            emitAscii(x.toLong())
        }
    }

    fun emitList(values: IntArray) {
        config.out(values.joinToString(","))
    }
}
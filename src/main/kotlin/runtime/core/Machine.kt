package runtime.core

/**
 * HOlds machine state and exposes helpers for ops to use
 */
class Machine(
    val config: RuntimeConfig
) {
    private val slots = LongArray(config.slots + 1)

    fun checkSlot(n: Int) {
        require(n in 1..config.slots) { "Slot $n is out of bounds (1..${config.slots})" }
    }

    fun get(n: Int): Long { checkSlot(n); return slots[n] }
    fun set(n: Int, v: Long) { checkSlot(n); slots[n] = v }

    fun emitNumber(v: Long) = config.out(v.toString())
    fun emitAscii(v: Long) = config.out(Effects.renderAscii(v, config))
}
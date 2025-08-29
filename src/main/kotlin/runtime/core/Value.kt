package runtime.core

sealed interface Value {
    data class Num(val v: Long) : Value
    data class Rat(val num: Long, val den: Long) : Value
    data class FloatStr(val text: String) : Value
    data class CharCode(val code: Long) : Value
    data class Sack(val items: IntArray) : Value
    data class Chest(val map: MutableMap<Int, Int>) : Value
}
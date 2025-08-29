package parse

sealed interface Operand {
    data class Slot(val n: Int) : Operand
    data class Item(val name: String) : Operand
    data class Number(val value: Int) : Operand
    data class Harvest(val sackSlot: Int, val index: Operand) : Operand

    data class Brew(
        val value: Operand,
        val target: BrewType,
        val rounding: Rounding? = null,
        val scale: Int? = null
    ) : Operand

    data class SackLiteral(val items: List<String>) : Operand
}
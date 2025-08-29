package parse

/**
 * Things you can compare in conditions
 */
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

enum class BrewType { INT, RAT, FLOAT, STRING }
enum class Rounding { FLOOR, CEIL, ROUND, TRUNC }

/**
 * Themed comparators
 * BEDROCK = "unbreakable equality", TNT = "explodes if values differ"
 */
enum class Cmp { BEDROCK_EQ, TNT_NE }
data class Condition(val left: Operand, val cmp: Cmp, val right: Operand)

/**
 * AST nodes representing high-level instructions
 */
sealed interface Instr {
    data class Place(val itemName: String, val slot: Int) : Instr
    data class CraftAdd(val a: Int, val b: Int, val c: Int) : Instr
    data class ShearSub(val a: Int, val b: Int, val c: Int) : Instr
    data class SmithMul(val a: Int, val b: Int, val c: Int) : Instr
    data class DisenchantDiv(val a: Int, val b: Int, val c: Int) : Instr

    data class Redstone(val cond: Condition, val thenBlock: List<Instr>, val elseBlock: List<Instr>?) : Instr
    data class Mine(val cond: Condition, val body: List<Instr>) : Instr
    data class Smelt(val countSlot: Int, val body: List<Instr>) : Instr
    data class Travel(val indexSlot: Int, val startSlot: Int, val endSlot: Int, val body: List<Instr>) : Instr

    data class PlaceSack(val slot: Int, val items: List<String>) : Instr
    data class SayExpr(val operand: Operand) : Instr
    data class Ask(val prompt: Operand, val dstSlot: Int) : Instr
    data class Length(val sackSlot: Int, val dst: Int) : Instr
    data class Trade(val sackSlot: Int, val index: Operand, val itemName: String) : Instr
    data class Sprint(val sackSlot: Int, val itemName: String) : Instr
    data class Sneak(val sackSlot: Int) : Instr

    data class Sleep(val ticks: Int) : Instr

    data class BrewInto(val value: Operand, val target: BrewType, val dstSlot: Int, val rounding: Rounding? = null, val scale: Int? = null) : Instr
}

/**
 * A full program = a list of instructions
 */
data class Program(val instructions: List<Instr>)
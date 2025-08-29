package parse

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
    data class PlaceString(val text: String, val slot: Int) : Instr
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
    data class Scribe(val src: Int, val dst: Int) : Instr
    data class Bind(val a: Int, val b: Int, val dst: Int) : Instr
    data class Loom(val sackSlot: Int, val index: Operand, val dst: Int) : Instr
    data class Flip(val sackSlot: Int, val dst: Int) : Instr

    data class Enchant(val sackSlot: Int, val upper: Boolean, val dst: Int) : Instr

    data class PlaceChest(val slot: Int, val entries: List<Pair<String, String>>) : Instr
    data class ChestStash(val chestSlot: Int, val key: Operand, val value: Operand) : Instr
    data class ChestRaid(val chestSlot: Int, val key: Operand, val dst: Int) : Instr

    data class Command(val name: String, val body: List<Instr>) : Instr
    data class Activate(val name: String, val args: List<Operand>, val dstSlots: List<Int>) : Instr

    data class BrewInto(val value: Operand, val target: BrewType, val dstSlot: Int, val rounding: Rounding? = null, val scale: Int? = null) : Instr
}

/**
 * A full program = a list of instructions
 */
data class Program(val instructions: List<Instr>)
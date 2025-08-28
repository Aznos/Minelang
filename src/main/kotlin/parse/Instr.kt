package parse

/**
 * Things you can compare in conditions
 */
sealed interface Operand {
    data class Slot(val n: Int) : Operand
    data class Item(val name: String) : Operand
    data class Number(val value: Int) : Operand
}

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
    /**
     * `say <itemName> [toString]`
     *
     * @property itemName The textual item name (e.g. "cobblestone")
     * @property toString If true, interpret the value as an ASCII code and print the character
     */
    data class SayItem(val itemName: String, val toString: Boolean) : Instr

    /**
     * `say slot <n> [toString]`
     *
     * @property slot The slot number (1..36)
     * @property toString If true, interpret the value as an ASCII code and print the character
     */
    data class SaySlot(val slot: Int, val toString: Boolean) : Instr

    /**
     * `place <itemName> in slot <n>`
     * Writes an items numerical ID to slot n (1..36)
     *
     * @property itemName The item name (e.g. "cobblestone")
     * @property slot The slot number (1..36)
     */
    data class Place(val itemName: String, val slot: Int) : Instr

    /**
     * `craft slot <a> with slot <b> into slot <c>`
     * slot[c] = slot[a] + slot[b]
     *
     * @property a The first slot number (1..36)
     * @property b The second slot number (1..36)
     * @property c The destination slot number (1..36)
     */
    data class CraftAdd(val a: Int, val b: Int, val c: Int) : Instr

    /**
     * `shear slot <a> from slot <b> into slot <c>`
     * slot[c] = slot[b] - slot[a]
     *
     * @property a The slot number to subtract (1..36)
     * @property b The slot number to subtract from (1..36)
     * @property c The destination slot number (1..36)
     */
    data class ShearSub(val a: Int, val b: Int, val c: Int) : Instr

    /**
     * `smith slot <a> with slot <b> into slot <c>`
     * slot[c] = slot[a] * slot[b]
     *
     * @property a The first slot number (1..36)
     * @property b The second slot number (1..36)
     * @property c The destination slot number (1..36)
     */
    data class SmithMul(val a: Int, val b: Int, val c: Int) : Instr

    /**
     * `disenchant slot <a> by slot <b> into slot <c>`
     * slot[c] = slot[a] / slot[b]
     *
     * @property a The slot number to divide (1..36)
     * @property b The slot number to divide by (1..36)
     * @property c The destination slot number (1..36)
     */
    data class DisenchantDiv(val a: Int, val b: Int, val c: Int) : Instr

    /**
     * `redstone <cond> then <thenBlock> [else <elseBlock>] end`
     * If condition basically
     *
     * @property cond The condition to evaluate
     * @property thenBlock The block of instructions to execute if the condition is true
     * @property elseBlock The block of instructions to execute if the condition is false (optional)
     */
    data class Redstone(val cond: Condition, val thenBlock: List<Instr>, val elseBlock: List<Instr>?) : Instr

    /**
     * `mind <cond> do <body> end`
     * Mines while the condition is true
     *
     * @property cond The condition to evaluate before each iteration
     * @property body The block of instructions to execute in each iteration
     */
    data class Mine(val cond: Condition, val body: List<Instr>) : Instr

    /**
     * `smelt slot <n> times do <body> end`
     * Repeats body exactly value(slot n) times (does not mutate slot)
     *
     * @property countSlot The slot number (1..36) containing the number of iterations
     * @property body The block of instructions to execute in each iteration
     */
    data class Smelt(val countSlot: Int, val body: List<Instr>) : Instr

    /**
     * `travel slot <i> from slot <a> to slot <b> do <body> end`
     * Initializes slot i to slot a, runs body each step, increments or decrements towards b
     */
    data class Travel(val indexSlot: Int, val startSlot: Int, val endSlot: Int, val body: List<Instr>) : Instr

    /**
     * `place sack in slot <n> contains [<item1>, <item2>, ...]`
     *
     * @property slot The slot number (1..36) to place the sack into
     * @property items The list of items to include in the sack
     */
    data class PlaceSack(val slot: Int, val items: List<String>) : Instr
}

/**
 * A full program = a list of instructions
 */
data class Program(val instructions: List<Instr>)
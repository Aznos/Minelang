package parse

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
}

/**
 * A full program = a list of instructions
 */
data class Program(val instructions: List<Instr>)
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
    data class Say(val itemName: String, val toString: Boolean) : Instr
}

/**
 * A full program = a list of instructions
 */
data class Program(val instructions: List<Instr>)
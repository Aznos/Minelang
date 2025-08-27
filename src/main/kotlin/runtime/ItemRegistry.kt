package runtime

/**
 * Maps items to their numeric IDs
 */
object ItemRegistry {
    private val byName: Map<String, Int> = mapOf(
        "dirt" to 3,
        "cobblestone" to 4
    )

    /**
     * Looks up the numeric ID of an item by its name (case insensitive)
     *
     * @param name The item name
     * @return The numeric ID, or null if not found
     */
    fun idOf(name: String): Int? = byName[name.lowercase()]
}
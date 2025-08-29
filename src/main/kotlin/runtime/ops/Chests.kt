package runtime.ops

import parse.Instr
import runtime.core.Machine
import runtime.core.Value
import runtime.registry.ItemRegistry

object Chests {
    fun handlePlaceChest(m: Machine, i: Instr.PlaceChest) {
        val map = mutableMapOf<Int, Int>()
        for((kName, vName) in i.entries) {
            val k = ItemRegistry.idOf(kName) ?: error("Unknown item: $kName")
            val v = ItemRegistry.idOf(vName) ?: error("Unknown item: $vName")
            map[k] = v
        }

        m.setValue(i.slot, Value.Chest(map))
    }

    fun handleStash(m: Machine, i: Instr.ChestStash) {
        val chest = m.getRaw(i.chestSlot) as? Value.Chest ?: error("No chest in slot ${i.chestSlot}")
        val k = toInt(m, Eval.evalOperand(m, i.key))
        val v = toInt(m, Eval.evalOperand(m, i.value))
        chest.map[k] = v
    }

    fun handleRaid(m: Machine, i: Instr.ChestRaid) {
        val chest = m.getRaw(i.chestSlot) as? Value.Chest ?: error("No chest in slot ${i.chestSlot}")
        val k = toInt(m, Eval.evalOperand(m, i.key))
        val v = chest.map[k] ?: 0
        m.setNum(i.dst, v.toLong())
    }

    private fun toInt(m: Machine, v: Value): Int {
        return when(v) {
            is Value.Num -> v.v.toInt()
            is Value.CharCode -> v.code.toInt()
            is Value.Rat -> {
                require(v.den != 0L && v.num % v.den == 0L) { "Expected integer, got $v" }
                (v.num / v.den).toInt()
            }
            is Value.FloatStr -> error("Expected integer, got $v")
            is Value.Chest -> error("Expected integer, got $v")
            is Value.Sack -> error("Expected integer, got $v")
        }
    }
}
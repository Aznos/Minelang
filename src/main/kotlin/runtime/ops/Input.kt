package runtime.ops

import parse.Instr
import parse.Operand
import runtime.core.Effects
import runtime.core.Machine
import runtime.core.Value
import runtime.registry.ItemRegistry

object Input {
    private val intPattern = Regex("^-?\\d+$")

    fun handleAsk(m: Machine, i: Instr.Ask) {
        val (promptText, promptIds) = when (val p = i.prompt) {
            is Operand.Item -> {
                val id = ItemRegistry.idOf(p.name) ?: error("Unknown item: ${p.name}")
                "" to intArrayOf(id)
            }

            is Operand.Slot -> {
                val v = m.getRaw(p.n)
                val sack = v as? Value.Sack ?: error("Slot ${p.n} does not contain a sack")
                val text = buildString {
                    for(id in sack.items) append(Effects.renderAscii(id.toLong(), m.config))
                }

                text to sack.items.copyOf()
            }

            is Operand.SackLiteral -> {
                val ids = p.items.map { name -> ItemRegistry.idOf(name) ?: error("Unknown item: $name")
                }.toIntArray()

                val text = buildString {
                    for(id in ids) append(Effects.renderAscii(id.toLong(), m.config))
                }
                text to ids
            }

            else -> error("Invalid prompt operand: $p")
        }

        m.emit(promptText)
        m.flush()

        val line = m.readLineOrNull() ?: error("End of input reached")
        if (intPattern.matches(line)) {
            val n = line.toLong()
            m.setNum(i.dstSlot, n)
            return
        }

        val replyIds = line.map { ch ->
            ItemRegistry.idOfChar(ch) ?: error("No item for character: '$ch'")
        }.toIntArray()

        m.setValue(i.dstSlot, Value.Sack(replyIds))
    }
}
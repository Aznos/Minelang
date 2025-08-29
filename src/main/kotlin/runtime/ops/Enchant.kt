package runtime.ops

import jdk.internal.org.jline.utils.Colors.s
import parse.Instr
import runtime.core.Machine
import runtime.core.Value
import runtime.registry.ItemRegistry

object Enchant {
    fun handleEnchant(m: Machine, i: Instr.Enchant) {
        val v = m.getRaw(i.sackSlot)
        val out: IntArray = when(v) {
            is Value.Sack -> {
                val src = v.items
                val mapped = IntArray(src.size)
                if(i.upper) {
                    var k = 0
                    while(k < src.size) {
                        val c = src[k]
                        mapped[k] = if(c in 97..122) c - 32 else c
                        k++
                    }
                } else {
                    var k = 0
                    while(k < src.size) {
                        val c = src[k]
                        mapped[k] = if(c in 65..90) c + 32 else c
                        k++
                    }
                }

                mapped
            }

            is Value.CharCode -> {
                val c = v.code
                val mapped = if(i.upper) {
                    if(c in 97..122) c - 32 else c
                } else {
                    if(c in 65..90) c + 32 else c
                }

                intArrayOf(mapped.toInt())
            }

            else -> throw RuntimeException("Can only enchant sacks or single characters, found $v")
        }

        m.setSack(i.dst, out)
    }
}
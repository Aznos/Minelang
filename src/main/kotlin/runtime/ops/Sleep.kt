package runtime.ops

import parse.Instr
import runtime.core.Machine

object Sleep {
    fun handleSleep(m: Machine, i: Instr.Sleep) {
        m.flush()
        val millis = i.ticks * 50L
        m.config.sleepFn(millis)
        m.flush()
    }
}
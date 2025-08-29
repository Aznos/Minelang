package runtime.core

import parse.Instr
import parse.Program
import runtime.ops.*

/**
 * Runs a program by dispatching each instruction to its handler
 */
class Execution(
    private val program: Program,
    private val machine: Machine
) {
    fun run() = runBlock(program.instructions)

    private fun runBlock(block: List<Instr>) {
        for(ins in block) {
            when(ins) {
                is Instr.Place -> Move.handlePlace(machine, ins)
                is Instr.SayExpr -> Say.handleSayExpr(machine, ins)
                is Instr.Ask -> Input.handleAsk(machine, ins)

                is Instr.CraftAdd -> Add.handleCraft(machine, ins)
                is Instr.ShearSub -> Sub.handleShear(machine, ins)
                is Instr.SmithMul -> Mul.handleSmith(machine, ins)
                is Instr.DisenchantDiv -> Div.handleDisenchant(machine, ins)

                is Instr.Redstone -> Control.handleRedstone(::runBlock, machine, ins)
                is Instr.Mine -> Control.handleMine(::runBlock, machine, ins)
                is Instr.Smelt -> Control.handleSmelt(::runBlock, machine, ins)
                is Instr.Travel -> Control.handleTravel(::runBlock, machine, ins)

                is Instr.PlaceSack -> Sack.handlePlaceSack(machine, ins)
                is Instr.Length -> Sack.handleLength(machine, ins)
                is Instr.Trade -> Sack.handleTrade(machine, ins)
                is Instr.Sprint -> Sack.handleSprint(machine, ins)
                is Instr.Sneak -> Sack.handleSneak(machine, ins)

                is Instr.Sleep -> Sleep.handleSleep(machine, ins)
                is Instr.Scribe -> Strings.handleScribe(machine, ins)
                is Instr.Bind -> Strings.handleBind(machine, ins)
                is Instr.Loom -> Strings.handleLoom(machine, ins)

                is Instr.BrewInto -> BrewExec.handleBrewInto(machine, ins.value, ins.target, ins.dstSlot, ins.rounding, ins.scale)
            }
        }
    }
}
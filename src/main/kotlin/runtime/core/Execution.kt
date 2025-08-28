package runtime.core

import parse.Instr
import parse.Program
import runtime.ops.Add
import runtime.ops.Control
import runtime.ops.Div
import runtime.ops.Move
import runtime.ops.Mul
import runtime.ops.Say
import runtime.ops.Sub

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
                is Instr.SayItem -> Say.handleSayItem(machine, ins)
                is Instr.SaySlot -> Say.handleSaySlot(machine, ins)

                is Instr.CraftAdd -> Add.handleCraft(machine, ins)
                is Instr.ShearSub -> Sub.handleShear(machine, ins)
                is Instr.SmithMul -> Mul.handleSmith(machine, ins)
                is Instr.DisenchantDiv -> Div.handleDisenchant(machine, ins)

                is Instr.Redstone -> Control.handleRedstone(::runBlock, machine, ins)
                is Instr.Mine -> Control.handleMine(::runBlock, machine, ins)
                is Instr.Smelt -> Control.handleSmelt(::runBlock, machine, ins)
                is Instr.Travel -> Control.handleTravel(::runBlock, machine, ins)
            }
        }
    }
}
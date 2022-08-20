package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.JumpOverShaman
import com.example.cairnclone.game.board.*

class Jumping(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is JumpOverShaman -> ActionResult.NothingToDo(
                listOf(
                    JumpOver(action.jumper, action.springboard),
                    FlipJumpTile,
                    TryActivateMonolith(
                        action.jumper.team,
                        jumpLandingPos(action.jumper, action.springboard)
                    ) { CompleteJump },
                )
            )
            is JumpOver -> jumpOver(action)
            is FlipJumpTile -> flipJumpTile()
            is TryActivateMonolith -> tryActivateMonolith(action)
            is CompleteJump -> completeJump()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun jumpLandingPos(
        jumper: Shaman,
        springboard: Shaman
    ): Pos {
        val dir = jumper.pos.adjacentDirection(springboard.pos)!!
        return jumper.pos + dir + dir
    }

    private fun jumpOver(action: JumpOver): ActionResult {
        val (jumper, springboard) = action
        val newPos = jumpLandingPos(jumper, springboard)
        return ActionResult.NewState(
            Jumping(
                boardState.copy(
                    activeShamans = boardState.activeShamans - jumper + jumper.copy(pos = newPos)
                )
            )
        )
    }

    private fun flipJumpTile(): ActionResult =
        ActionResult.NewState(
            Jumping(
                boardState.copy(
                    jumpActionTile = boardState.jumpActionTile.flip()
                )
            )
        )

    private fun tryActivateMonolith(action: TryActivateMonolith): ActionResult =
        tryActivatingMonolith(
            action.pos,
            action.team,
            { ActionResult.NewState(Jumping(it), listOf(action.nextAction())) },
            boardState
        )

    private fun completeJump(): ActionResult =
        ActionResult.NewState(WaitForTransformation(boardState))

    private data class JumpOver(val jumper: Shaman, val springboard: Shaman) : Action
    private object FlipJumpTile : Action
    private data class TryActivateMonolith(
        val team: Team,
        val pos: Pos,
        val nextAction: () -> Action
    ) : Action

    private object CompleteJump : Action
}

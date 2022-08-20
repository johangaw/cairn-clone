package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.JumpOverShaman
import com.example.cairnclone.game.board.*

class Jumping(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is JumpOverShaman -> ActionResult.NothingToDo(
                listOf(
                    Jump(action.jumper, action.newPos),
                    FlipJumpTile,
                    TryActivateMonolith(action.jumper.team, action.newPos)
                    { CompleteJump },
                )
            )
            is Jump -> jump(action)
            is FlipJumpTile -> flipJumpTile()
            is TryActivateMonolith -> tryActivateMonolith(action)
            is CompleteJump -> completeJump()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun jump(action: Jump): ActionResult {
        return ActionResult.NewState(
            Jumping(
                boardState.copy(
                    activeShamans = boardState.activeShamans - action.jumper + action.jumper.copy(
                        pos = action.newPos
                    )
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

    private data class Jump(val jumper: Shaman, val newPos: Pos) : Action
    private object FlipJumpTile : Action
    private data class TryActivateMonolith(
        val team: Team,
        val pos: Pos,
        val nextAction: () -> Action
    ) : Action

    private object CompleteJump : Action
}

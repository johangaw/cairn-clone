package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.MoveShaman
import com.example.cairnclone.game.board.*

class Moving(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is MoveShaman -> {
                ActionResult.NewState(
                    this,
                    listOf(
                        Move(action.shaman, action.newPos),
                        MarkShamanAsMoved(action.shaman),
                        FlipMoveTile,
                        TryStartBuildMonolith(action.shaman.pos, action.newPos, action.shaman.team),
                    )
                )
            }
            is Move -> handleMove(action)
            is MarkShamanAsMoved -> handleMarkShamanAsMoved(action)
            is FlipMoveTile -> handleFlipMoveTile()
            is TryStartBuildMonolith -> handleTryStartBuildMonolith(action, TryActivateMonolith(action.moveToPos, action.team))
            is TryActivateMonolith -> handleTryActivateMonolith(action, CompleteMoving)
            is CompleteMoving -> handleCompleteMoving()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun handleMove(action: Move) = ActionResult.NewState(
        Moving(
            boardState.copy(
                activeShamans = boardState.activeShamans.map {
                    if (it == action.shaman) it.copy(pos = action.newPos) else it
                }
            )
        )
    )

    private fun handleMarkShamanAsMoved(action: MarkShamanAsMoved): ActionResult =
        ActionResult.NewState(Moving(boardState.copy(movedShamanIds = boardState.movedShamanIds + action.shaman.id)))

    private fun handleFlipMoveTile() =
        ActionResult.NewState(Moving(boardState.copy(moveActionTile = boardState.moveActionTile.flip())))

    private fun handleTryStartBuildMonolith(
        action: TryStartBuildMonolith,
        vararg next: Action,
    ): ActionResult =
        if (boardState.isInVillage(action.moveToPos, action.team.other()))
            tryBuildMonolith(
                action.moveFromPos,
                action.team,
                boardState,
            ) { ActionResult.NewState(Moving(it), next.toList()) }
        else ActionResult.NothingToDo(next.toList())

    private fun handleTryActivateMonolith(
        action: TryActivateMonolith,
        vararg next: Action,
    ): ActionResult =
        tryActivatingMonolith(
            action.pos,
            action.team,
            { ActionResult.NewState(Moving(it), next.toList()) },
            boardState
        )

    private fun handleCompleteMoving() = ActionResult.NewState(WaitForTransformation(boardState))
}

private data class Move(val shaman: Shaman, val newPos: Pos) : Action
private data class MarkShamanAsMoved(val shaman: Shaman) : Action
private object FlipMoveTile : Action
private data class TryStartBuildMonolith(val moveFromPos: Pos, val moveToPos: Pos, val team: Team) : Action
private data class TryActivateMonolith(val pos: Pos, val team: Team) : Action
private object CompleteMoving : Action

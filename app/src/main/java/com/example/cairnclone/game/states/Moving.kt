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
                        TryStartBuildMonolith(action.shaman.pos, action.shaman.team),
                        TryActivateMonolith(action.newPos, action.shaman.team),
                        CompleteMoving
                    )
                )
            }
            is Move -> handleMove(action)
            is MarkShamanAsMoved -> handleMarkShamanAsMoved(action)
            is FlipMoveTile -> handleFlipMoveTile()
            is TryStartBuildMonolith -> handleTryStartBuildMonolith(action)
            is TryActivateMonolith -> handleTryActivateMonolith(action)
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
    ): ActionResult =
        if (boardState.isInVillage(action.pos, action.team.other()))
            tryBuildMonolith(
                action.pos,
                action.team,
                boardState,
            ) { ActionResult.NewState(Moving(it)) }
        else ActionResult.NothingToDo

    private fun handleTryActivateMonolith(
        action: TryActivateMonolith
    ): ActionResult =
        tryActivatingMonolith(
            action.pos,
            action.team,
            { ActionResult.NewState(Moving(it)) },
            boardState
        )

    private fun handleCompleteMoving() = ActionResult.NewState(WaitForTransformation(boardState))
}

private data class Move(val shaman: Shaman, val newPos: Pos) : Action
private data class MarkShamanAsMoved(val shaman: Shaman) : Action
private object FlipMoveTile : Action
private data class TryStartBuildMonolith(val pos: Pos, val team: Team) : Action
private data class TryActivateMonolith(val pos: Pos, val team: Team) : Action
private object CompleteMoving : Action

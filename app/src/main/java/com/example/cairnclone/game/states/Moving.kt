package com.example.cairnclone.game.states

import com.example.cairnclone.game.*
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.BuildMonolith
import com.example.cairnclone.game.actions.MoveShaman

class Moving(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is MoveShaman -> {
                val shaman = boardState.activeShaman(action.shamanId)!!
                ActionResult.NewState(
                    this,
                    listOfNotNull(
                        Move(shaman, action.newPos),
                        FlipMoveTile,
                        if (boardState.isInVillage(
                                action.newPos,
                                shaman.team.other()
                            )
                        ) BuildMonolith(shaman.pos, shaman.team) else null,
                        CompleteMoving
                    )
                )
            }
            is Move -> handleMove(action)
            is FlipMoveTile -> handleFlipMoveTile()
            is CompleteMoving -> handleCompleteMoving()
            is BuildMonolith -> handleSpawnMonolith(action)
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun handleSpawnMonolith(action: BuildMonolith): ActionResult = ActionResult.NewState(
        BuildingMonolith({ ActionResult.NewState(Moving(it)) }, boardState),
        listOf(action)
    )

    private fun handleCompleteMoving() = ActionResult.NewState(ActivatingMonolith(boardState))

    private fun handleFlipMoveTile() =
        ActionResult.NewState(Moving(boardState.copy(moveActionTile = boardState.moveActionTile.flip())))

    private fun handleMove(action: Move) = ActionResult.NewState(
        Moving(
            boardState.copy(
                activeShamans = boardState.activeShamans.map {
                    if (it == action.shaman) it.copy(pos = action.newPos) else it
                }
            )
        )
    )
}

private fun MoveActionTile.flip(): MoveActionTile = when (this) {
    is MoveActionTile.Diagonally -> MoveActionTile.Orthogonally
    is MoveActionTile.Orthogonally -> MoveActionTile.Diagonally
}

private data class Move(val shaman: Shaman, val newPos: Pos) : Action
private object FlipMoveTile : Action
private object CompleteMoving : Action

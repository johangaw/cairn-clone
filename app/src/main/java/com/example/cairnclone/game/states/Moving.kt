package com.example.cairnclone.game.states

import com.example.cairnclone.game.*
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.MoveShaman

class Moving(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is MoveShaman -> {
                ActionResult.NewState(
                    this,
                    listOfNotNull(
                        Move(action.shaman, action.newPos),
                        FlipMoveTile,
                        if (boardState.isInVillage(
                                action.newPos,
                                action.shaman.team.other()
                            )
                        ) StartBuildMonolith(action.shaman.pos, action.shaman.team) else null
                    )
                )
            }
            is Move -> handleMove(action)
            is FlipMoveTile -> handleFlipMoveTile()
            is StartBuildMonolith -> handleStartBuildMonolith(action)
            is CompleteMoving -> handleCompleteMoving()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun handleStartBuildMonolith(action: StartBuildMonolith): ActionResult =
        tryBuildMonolith(
            action.pos,
            action.team,
            boardState,
        ) { ActionResult.NewState(Moving(it), listOf(CompleteMoving)) }


    private fun handleCompleteMoving() = tryActivatingMonolith(
        Pos(0, 0),
        { ActionResult.NewState(WaitForTransformation(boardState)) },
        boardState
    )

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
private data class StartBuildMonolith(val pos: Pos, val team: Team) : Action

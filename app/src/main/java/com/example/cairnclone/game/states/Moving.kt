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
                        FlipMoveTile,
                        if (boardState.isInVillage(
                                action.newPos,
                                action.shaman.team.other()
                            )
                        ) StartBuildMonolith(action.shaman.pos, action.shaman.team)
                        else CompleteMoving
                    )
                )
            }
            is Move -> handleMove(action)
            is FlipMoveTile -> handleFlipMoveTile()
            is StartBuildMonolith -> handleStartBuildMonolith(
                action,
                ActivateMonolith(action.pos, action.team)
            )
            is ActivateMonolith -> handleActivateMonolith(action, CompleteMoving)
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

    private fun handleFlipMoveTile() =
        ActionResult.NewState(Moving(boardState.copy(moveActionTile = boardState.moveActionTile.flip())))

    private fun handleStartBuildMonolith(
        action: StartBuildMonolith,
        vararg nextActions: Action
    ): ActionResult =
        tryBuildMonolith(
            action.pos,
            action.team,
            boardState,
        ) { ActionResult.NewState(Moving(it), nextActions.toList()) }

    private fun handleActivateMonolith(
        action: ActivateMonolith,
        vararg nextActions: Action
    ): ActionResult =
        tryActivatingMonolith(
            action.pos,
            { ActionResult.NewState(Moving(it), nextActions.toList()) },
            boardState
        )

    private fun handleCompleteMoving() = ActionResult.NewState(WaitForTransformation(boardState))
}

private data class Move(val shaman: Shaman, val newPos: Pos) : Action
private object FlipMoveTile : Action
private data class StartBuildMonolith(val pos: Pos, val team: Team) : Action
private data class ActivateMonolith(val pos: Pos, val team: Team) : Action
private object CompleteMoving : Action

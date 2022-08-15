package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.EndTurn
import com.example.cairnclone.game.actions.TransformShaman
import com.example.cairnclone.game.board.*

class Transforming(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is TransformShaman -> ActionResult.NewState(
                this, listOf(
                    RemoveShaman(action.target),
                    FlipTransformationTile,
                    StartBuildMonolith(action.target.pos, action.target.team.other())
                )
            )
            is RemoveShaman -> handleRemoveShaman(action)
            is FlipTransformationTile -> handleFlipTransformationTile()
            is StartBuildMonolith -> handleStartBuildMonolith(action)
            is CompleteTransforming -> handleCompleteTransforming()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun handleRemoveShaman(action: RemoveShaman): ActionResult =
        ActionResult.NewState(
            Transforming(
                boardState.copy(
                    activeShamans = boardState.activeShamans - action.shaman,
                    inactiveShamans = boardState.inactiveShamans + action.shaman
                )
            )
        )

    private fun handleFlipTransformationTile(): ActionResult =
        ActionResult.NewState(
            Transforming(
                boardState.copy(
                    transformationTile = boardState.transformationTile.flip()
                )
            )
        )

    private fun handleStartBuildMonolith(action: StartBuildMonolith): ActionResult =
        tryBuildMonolith(
            action.pos,
            action.team,
            boardState,
        ) { ActionResult.NewState(Transforming(it), listOf(CompleteTransforming)) }

    private fun handleCompleteTransforming(): ActionResult =
        ActionResult.NewState(EndingTurn(boardState), listOf(EndTurn))

    private data class RemoveShaman(val shaman: Shaman) : Action
    private object FlipTransformationTile : Action
    private data class StartBuildMonolith(val pos: Pos, val team: Team) : Action
    private object CompleteTransforming : Action
}
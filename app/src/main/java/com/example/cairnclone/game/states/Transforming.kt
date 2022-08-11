package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.Shaman
import com.example.cairnclone.game.TransformationTile
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.BuildMonolith
import com.example.cairnclone.game.actions.EndTurn
import com.example.cairnclone.game.actions.TransformShaman
import com.example.cairnclone.game.other

class Transforming(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is TransformShaman -> ActionResult.NewState(this, listOf(
                RemoveShaman(action.target),
                FlipTransformationTile,
                BuildMonolith(action.target.pos, action.target.team.other())
            ))
            is RemoveShaman -> handleRemoveShaman(action)
            is FlipTransformationTile -> handleFlipTransformationTile()
            is BuildMonolith -> handleBuildMonolith(action)
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

    private fun handleBuildMonolith(action: BuildMonolith): ActionResult =
        ActionResult.NewState(BuildingMonolith({
            ActionResult.NewState(
                EndingTurn(it),
                listOf(EndTurn)
            )
        }, boardState), listOf(action))

    private data class RemoveShaman(val shaman: Shaman) : Action
    private object FlipTransformationTile : Action
}

private fun TransformationTile.flip(): TransformationTile =
    when (this) {
        is TransformationTile.Outnumbered -> TransformationTile.Surrounded
        is TransformationTile.Surrounded -> TransformationTile.Outnumbered
    }
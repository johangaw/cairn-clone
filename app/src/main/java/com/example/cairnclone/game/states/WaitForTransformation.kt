package com.example.cairnclone.game.states

import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.EndTurn
import com.example.cairnclone.game.actions.TransformShaman

class WaitForTransformation(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is EndTurn -> handleEndTurn(action)
            is TransformShaman -> validateTransformation(action)
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun validateTransformation(action: TransformShaman): ActionResult {
        val trans1 = action.trans1
        val trans2 = action.trans2
        val target = action.target
        return when {
            !boardState.activeShamans.contains(trans1) -> ActionResult.InvalidAction(" $trans1 is not an active shaman")
            !boardState.activeShamans.contains(trans2) -> ActionResult.InvalidAction(" $trans2 is not an active shaman")
            !boardState.activeShamans.contains(target) -> ActionResult.InvalidAction(" $target is not an active shaman")
            trans1.team != boardState.activeTeam -> ActionResult.InvalidAction("$trans1 is not of the active team")
            trans2.team != boardState.activeTeam -> ActionResult.InvalidAction("$trans2 is not of the active team")
            target.team == boardState.activeTeam -> ActionResult.InvalidAction("$target is of the active team")
            listOf(trans1, trans2, target).all { !boardState.movedShamanIds.contains(it.id) } -> ActionResult.InvalidAction("none of the selected shamans have been moved this turn")
            !boardState.transformationTile.isApplicable(
                trans1.pos,
                trans2.pos,
                target.pos
            ) -> ActionResult.InvalidAction("unable to perform a ${boardState.transformationTile.javaClass.simpleName} transformation from positions ${trans1.pos} + ${trans2.pos} => ${target.pos}")

            else -> ActionResult.NewState(Transforming(boardState), listOf(action))
        }
    }

    private fun handleEndTurn(action: EndTurn): ActionResult =
        ActionResult.NewState(EndingTurn(boardState), listOf(action))

}
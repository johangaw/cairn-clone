package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
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
        val trans1 = boardState.activeShaman(action.trans1)
        val trans2 = boardState.activeShaman(action.trans2)
        val target = boardState.activeShaman(action.target)
        return when {
            trans1 == null -> ActionResult.InvalidAction("no active shaman with id ${action.trans1}")
            trans2 == null -> ActionResult.InvalidAction("no active shaman with id ${action.trans2}")
            target == null -> ActionResult.InvalidAction("no active shaman with id ${action.target}")
            trans1.team != boardState.activeTeam -> ActionResult.InvalidAction("$trans1 is not of the active team")
            trans2.team != boardState.activeTeam -> ActionResult.InvalidAction("$trans2 is not of the active team")
            target.team == boardState.activeTeam -> ActionResult.InvalidAction("$target is of the active team")
            !boardState.transformationTile.isApplicable(
                trans1.pos,
                trans2.pos,
                target.pos
            ) -> ActionResult.InvalidAction("unable to perform a ${boardState.transformationTile.javaClass.simpleName} transformation from positions ${trans1.pos} + ${trans2.pos} => ${target.pos}")

            else -> ActionResult.NewState(Transforming(boardState))
        }
    }

    private fun handleEndTurn(action: EndTurn): ActionResult =
        ActionResult.NewState(EndingTurn(boardState), listOf(action))

}
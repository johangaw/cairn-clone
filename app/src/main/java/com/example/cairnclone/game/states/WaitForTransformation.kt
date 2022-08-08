package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.EndTurn
import com.example.cairnclone.game.other

class WaitForTransformation(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is EndTurn -> handleEndTurn()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun handleEndTurn(): ActionResult =
        ActionResult.NewState(WaitForAction(boardState.copy(activeTeam = boardState.activeTeam.other())))

}
package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.EndTurn
import com.example.cairnclone.game.other

class EndingTurn(boardState: BoardState) : GameState(boardState) {

    override fun perform(action: Action): ActionResult {
        return when (action) {
            is EndTurn -> handleEndTurn()
            is SwitchActiveTeam -> handleSwitchActiveTeam()
            is ResetMovedShamans -> ActionResult.NothingToDo // TODO: use this later
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun handleEndTurn(): ActionResult =
        ActionResult.NewState(this, listOf(SwitchActiveTeam, RestartTurn))

    private fun handleSwitchActiveTeam(): ActionResult =
        ActionResult.NewState(EndingTurn(boardState.copy(activeTeam = boardState.activeTeam.other())))

}

private object SwitchActiveTeam : Action
private object ResetMovedShamans : Action
private object RestartTurn : Action
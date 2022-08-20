package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Team
import com.example.cairnclone.game.board.toShaman
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState

class ActivateCairnOfDawn(
    boardState: BoardState,
    val team: Team,
    val nextState: (boardState: BoardState) -> ActionResult.NewState
) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is Activate -> activate(action)
            is Skipp -> skipp()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun activate(action: Activate): ActionResult {
        val inactiveShaman = boardState.inactiveShamans.find { it.team == team }
        return when {
            inactiveShaman == null -> ActionResult.InvalidAction("no inactive shamans to spawn")
            boardState.shamanAt(action.pos) != null -> ActionResult.InvalidAction("the selected pos ${action.pos} is occupied")
            action.pos !in boardState.board.firstRowFor(team) -> ActionResult.InvalidAction("the selected pos ${action.pos} it not in the first row of $team")
            else -> nextState(
                boardState.copy(
                    inactiveShamans = boardState.inactiveShamans - inactiveShaman,
                    activeShamans = boardState.activeShamans + inactiveShaman.toShaman(action.pos)
                )
            )
        }
    }


    private fun skipp(): ActionResult =
        if (
            boardState.board.firstRowFor(team).any { boardState.shamanAt(it) == null } &&
            boardState.inactiveShamans.any { it.team == team }
        ) ActionResult.InvalidAction("it is possible to spawn a shaman in the first row of team $team")
        else nextState(boardState)

    data class Activate(val pos: Pos) : Action
    object Skipp : Action
}
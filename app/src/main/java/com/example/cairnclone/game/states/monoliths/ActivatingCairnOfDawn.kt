package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Team
import com.example.cairnclone.game.board.toShaman
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState

class ActivatingCairnOfDawn(
    boardState: BoardState,
    val team: Team,
    val nextState: (boardState: BoardState) -> ActionResult.NewState
) : GameState(boardState), MonolithGameState {

    override val monolith: MonolithType = MonolithType.CairnOfDawn

    override fun canActivate(): Boolean =
        boardState.board.firstRowFor(team).any { boardState.shamanAt(it) == null } &&
                boardState.inactiveShamans.any { it.team == team }

    override fun perform(action: Action): ActionResult {
        return when (action) {
            is Activate -> activate(action)
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

    data class Activate(val pos: Pos) : Action
}
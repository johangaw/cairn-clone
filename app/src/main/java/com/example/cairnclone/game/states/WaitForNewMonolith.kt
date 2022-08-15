package com.example.cairnclone.game.states

import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Team
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.SelectMonolith

class WaitForNewMonolith(
    private val newMonolithPos: Pos,
    private val newMonolithTeam: Team,
    private val nextState: (boardState: BoardState) -> ActionResult.NewState,
    boardState: BoardState
) : GameState(boardState) {

    override fun perform(action: Action): ActionResult {
        return when (action) {
            is SelectMonolith -> validateSelectMonolith(action)
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun validateSelectMonolith(action: SelectMonolith): ActionResult {
        return when {
            !boardState.upcomingMonoliths.contains(action.monolithType) -> ActionResult.InvalidAction("${action.monolithType} is not an upcoming monolith")
            else -> ActionResult.NewState(BuildingMonolith(newMonolithPos, newMonolithTeam, nextState, boardState), listOf(action))
        }
    }
}


fun tryBuildMonolith(
    newMonolithPos: Pos,
    newMonolithTeam: Team,
    boardState: BoardState,
    nextState: (boardState: BoardState) -> ActionResult.NewState
): ActionResult.NewState =
    if (boardState.monolithAt(newMonolithPos) != null)
        nextState(boardState)
    else ActionResult.NewState(
        WaitForNewMonolith(
            newMonolithPos,
            newMonolithTeam,
            nextState,
            boardState
        )
    )


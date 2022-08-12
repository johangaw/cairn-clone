package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.Pos
import com.example.cairnclone.game.Team
import com.example.cairnclone.game.actions.Action

class BuildingMonolith(
    private val newMonolithPos: Pos,
    private val newMonolithTeam: Team,
    private val nextState: (boardState: BoardState) -> ActionResult.NewState,
    boardState: BoardState
): GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        TODO("Not yet implemented")
    }
}
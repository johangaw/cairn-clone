package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.actions.Action

class BuildingMonolith(val nextState: (boardState: BoardState) -> ActionResult.NewState, boardState: BoardState): GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        TODO("Not yet implemented")
    }
}
package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.Pos
import com.example.cairnclone.game.actions.Action

class ActivatingMonolith(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        TODO("Not implemented")
    }
}

fun tryActivatingMonolith(
    pos: Pos,
    nextState: (boardState: BoardState) -> ActionResult.NewState,
    boardState: BoardState
): ActionResult.NewState {
    // FIXME check if on monolith
    return nextState(boardState)
}
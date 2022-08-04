package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.actions.Action

class ActivatingMonolith(boardState: BoardState): GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        // FIXME
        return ActionResult.NewState(WaitForTransformation(boardState))
    }
}
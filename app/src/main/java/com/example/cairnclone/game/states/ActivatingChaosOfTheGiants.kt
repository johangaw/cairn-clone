package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Team

class ActivatingChaosOfTheGiants(boardState: BoardState, pos: Pos, team: Team): GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when(action) {
            // TODO test drive this!!!
            else -> ActionResult.NothingToDo
        }
    }
}


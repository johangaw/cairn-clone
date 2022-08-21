package com.example.cairnclone.game.states

import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Team
import com.example.cairnclone.game.states.monoliths.ActivatingCairnOfDawn
import com.example.cairnclone.game.states.monoliths.ActivatingChaosOfTheGiants

fun tryActivatingMonolith(
    pos: Pos,
    team: Team,
    nextState: (boardState: BoardState) -> ActionResult.NewState,
    boardState: BoardState
): ActionResult.NewState {
    val monolith = boardState.monolithAt(pos)
    return when (monolith?.type) {
        MonolithType.ChaosOfTheGiants -> ActivatingChaosOfTheGiants(
            boardState,
            team,
            nextState
        ).let { if (it.canActivate()) ActionResult.NewState(it) else nextState(boardState) }
        MonolithType.CairnOfDawn -> ActivatingCairnOfDawn(
            boardState,
            team,
            nextState
        ).let { if (it.canActivate()) ActionResult.NewState(it) else nextState(boardState) }
        else -> nextState(boardState)
    }
}
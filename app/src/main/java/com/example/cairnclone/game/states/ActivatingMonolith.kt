package com.example.cairnclone.game.states

import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Team

fun tryActivatingMonolith(
    pos: Pos,
    team: Team,
    nextState: (boardState: BoardState) -> ActionResult.NewState,
    boardState: BoardState
): ActionResult.NewState {
    val monolith = boardState.monolithAt(pos)
    return when(monolith?.type) {
        MonolithType.ChaosOfTheGiants -> ActionResult.NewState(ActivatingChaosOfTheGiants(boardState, team, nextState))
        else -> nextState(boardState)
    }
}
package com.example.cairnclone.game.states

import android.util.Log
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.states.monoliths.*

fun tryActivatingMonolith(
    pos: Pos,
    nextState: NextState,
    boardState: BoardState
): ActionResult.NewState {
    val shaman = boardState.shamanAt(pos)
    if(shaman == null) {
        Log.w("tryActivatingMonolith", "position $pos does not contain a shaman")
        return nextState(boardState)
    }

    val monolith = boardState.monolithAt(pos)
    return when (monolith?.type) {
        MonolithType.ChaosOfTheGiants -> ActivatingChaosOfTheGiants(
            boardState, monolith, shaman, nextState
        ).let { if (it.canActivate()) ActionResult.NewState(it) else nextState(boardState) }
        MonolithType.CairnOfDawn -> ActivatingCairnOfDawn(
            boardState, monolith, shaman, nextState
        ).let { if (it.canActivate()) ActionResult.NewState(it) else nextState(boardState) }
        MonolithType.CromlechOfTheStars -> ActivatingCromlechOfTheStars(
            boardState, monolith, shaman, nextState
        ).let { if (it.canActivate()) ActionResult.NewState(it) else nextState(boardState) }
        MonolithType.PillarsOfSpring -> ActivatingPillarsOfSpring(
            boardState, monolith, shaman, nextState
        ).let { if (it.canActivate()) ActionResult.NewState(it) else nextState(boardState) }
        MonolithType.AlleyOfDusk -> ActivatingAlleyOfDusk(
            boardState, monolith, shaman, nextState
        ).let { if (it.canActivate()) ActionResult.NewState(it) else nextState(boardState) }
        else -> nextState(boardState)
    }
}
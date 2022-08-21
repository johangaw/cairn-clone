package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Team
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState

class ActivatingPillarsOfSpring(
    boardState: BoardState,
    val team: Team,
    val nextState: (boardState: BoardState) -> ActionResult.NewState
) : GameState(boardState), MonolithGameState {

    override val monolith: MonolithType = MonolithType.PillarsOfSpring

    override fun canActivate(): Boolean = true

    override fun perform(action: Action): ActionResult =
        when (action) {
            is MakeNextTurnMyTurn -> makeNextTurnMyTurn()
            else -> ActionResult.InvalidAction(this, action)
        }

    private fun makeNextTurnMyTurn(): ActionResult =
        nextState(boardState.copy(nextActiveTeam = team))


    object MakeNextTurnMyTurn : Action
}
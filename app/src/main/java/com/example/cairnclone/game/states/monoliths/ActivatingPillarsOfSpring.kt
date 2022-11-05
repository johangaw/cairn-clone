package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Monolith
import com.example.cairnclone.game.board.MonolithType
import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState
import com.example.cairnclone.game.states.NextState

class ActivatingPillarsOfSpring(
    override val boardState: BoardState,
    override val monolith: Monolith,
    override val shaman: Shaman,
    override val nextState: NextState,
) : GameState, MonolithGameState {

    init {
        require(isValid(MonolithType.PillarsOfSpring))
    }

    override fun canActivate(): Boolean = true

    override fun perform(action: Action): ActionResult =
        when (action) {
            is MakeNextTurnMyTurn -> makeNextTurnMyTurn()
            else -> ActionResult.InvalidAction(this, action)
        }

    private fun makeNextTurnMyTurn(): ActionResult =
        nextState(boardState.copy(nextActiveTeam = shaman.team))


    object MakeNextTurnMyTurn : Action
}
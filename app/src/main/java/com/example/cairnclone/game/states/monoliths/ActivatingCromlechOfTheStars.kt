package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Monolith
import com.example.cairnclone.game.board.MonolithType
import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState
import com.example.cairnclone.game.states.NextState
import com.example.cairnclone.game.states.tryActivatingMonolith

class ActivatingCromlechOfTheStars(
    override val boardState: BoardState,
    override val monolith: Monolith,
    override val shaman: Shaman,
    override val nextState: NextState,
) : GameState, MonolithGameState {

    init {
        require(isValid(MonolithType.CromlechOfTheStars))
    }

    override fun canActivate(): Boolean =
        boardState.activeMonoliths.any {
            it.type != MonolithType.CromlechOfTheStars &&
                    boardState.shamanAt(it.pos) == null
        }


    override fun perform(action: Action): ActionResult =
        when (action) {
            is MoveToMonolith -> moveToMonolith(action)
            else -> ActionResult.InvalidAction(this, action)
        }

    private fun moveToMonolith(action: MoveToMonolith): ActionResult {
        return when {
            action.monolith !in boardState.activeMonoliths -> ActionResult.InvalidAction("the selected monolith is not active")
            boardState.shamanAt(action.monolith.pos) != null -> ActionResult.InvalidAction("the selected monolith is occupied ${action.monolith}")
            else -> tryActivatingMonolith(action.monolith.pos, nextState, boardState.copy(
                activeShamans = boardState.activeShamans - shaman + shaman.copy(pos = action.monolith.pos)
            ))
        }
    }


    data class MoveToMonolith(val monolith: Monolith) : Action
}
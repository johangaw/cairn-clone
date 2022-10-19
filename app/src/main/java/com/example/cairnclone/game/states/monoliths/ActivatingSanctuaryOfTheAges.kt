package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Monolith
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.board.allAdjacent
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState
import com.example.cairnclone.game.states.NextState
import com.example.cairnclone.game.states.isOnBoard

class ActivatingSanctuaryOfTheAges(
    override val boardState: BoardState,
    override val monolith: Monolith,
    override val shaman: Shaman,
    override val nextState: NextState,
) : GameState, MonolithGameState {

    init {
        require(isValid(MonolithType.SanctuaryOfTheAges))
    }

    override fun canActivate(): Boolean {
        return monolith.pos.allAdjacent().filter(boardState.board::isOnBoard)
            .any { boardState.monolithAt(it) == null }
    }

    override fun perform(action: Action): ActionResult {
        return when(action) {
            is MoveMonolith -> moveMonolith(action)
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun moveMonolith(action: MoveMonolith): ActionResult {
        return when {
            boardState.monolithAt(action.pos) != null -> ActionResult.InvalidAction("the selected position ${action.pos} already contains a monolith $monolith")
            !boardState.board.isOnBoard(action.pos) -> ActionResult.InvalidAction("the selected position ${action.pos} is not on  the board")
            else -> nextState(boardState.moveMonolith(monolith, action.pos))
        }
    }

    data class MoveMonolith(val pos: Pos) : Action
}

private fun BoardState.moveMonolith(monolith: Monolith, to: Pos): BoardState = this.copy(
    activeMonoliths = activeMonoliths - monolith + monolith.copy(pos = to)
)
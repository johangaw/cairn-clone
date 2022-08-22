package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Monolith
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.board.toShaman
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState
import com.example.cairnclone.game.states.NextState
import com.example.cairnclone.game.states.tryActivatingMonolith

class ActivatingCairnOfDawn(
    override val boardState: BoardState,
    override val monolith: Monolith,
    override val shaman: Shaman,
    override val nextState: NextState,
) : GameState, MonolithGameState {

    init {
        require(monolith.type == MonolithType.CairnOfDawn)
    }

    override fun canActivate(): Boolean =
        boardState.board.firstRowFor(shaman.team).any { boardState.shamanAt(it) == null } &&
                boardState.inactiveShamans.any { it.team == shaman.team }

    override fun perform(action: Action): ActionResult {
        return when (action) {
            is Activate -> activate(action)
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun activate(action: Activate): ActionResult {
        val inactiveShaman = boardState.inactiveShamans.find { it.team == shaman.team }
        return when {
            inactiveShaman == null -> ActionResult.InvalidAction("no inactive shamans to spawn")
            boardState.shamanAt(action.pos) != null -> ActionResult.InvalidAction("the selected pos ${action.pos} is occupied")
            action.pos !in boardState.board.firstRowFor(shaman.team) -> ActionResult.InvalidAction("the selected pos ${action.pos} it not in the first row of ${shaman.team}")
            else -> tryActivatingMonolith(
                action.pos, nextState, boardState.copy(
                    inactiveShamans = boardState.inactiveShamans - inactiveShaman,
                    activeShamans = boardState.activeShamans + inactiveShaman.toShaman(action.pos),
                    movedShamanIds = boardState.movedShamanIds + inactiveShaman.id
                )
            )
        }
    }

    data class Activate(val pos: Pos) : Action
}


package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Monolith
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.board.isAdjacent
import com.example.cairnclone.game.board.toInactiveShaman
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState
import com.example.cairnclone.game.states.NextState

class ActivatingAlleyOfDusk(
    override val boardState: BoardState,
    override val monolith: Monolith,
    override val shaman: Shaman,
    override val nextState: NextState
) : GameState, MonolithGameState {

    init {
        require(isValid(MonolithType.AlleyOfDusk))
    }

    @Suppress("ReplaceSizeCheckWithIsNotEmpty")
    override fun canActivate(): Boolean =
        boardState.activeShamans
        .filter { it.team != shaman.team }
        .count { it.pos.isAdjacent(shaman.pos) } > 0

    override fun perform(action: Action): ActionResult =
        when(action) {
            is BanishShaman -> banishShaman(action)
            else -> ActionResult.InvalidAction(this, action)
        }

    private fun banishShaman(action: BanishShaman): ActionResult =
        when {
            action.shaman !in boardState.activeShamans -> ActionResult.InvalidAction("the selected shaman ${action.shaman} is not active")
            action.shaman.team == shaman.team -> ActionResult.InvalidAction("the selected shaman ${action.shaman} must be from the other team")
            !monolith.pos.isAdjacent(action.shaman.pos) -> ActionResult.InvalidAction("the selected shaman ${action.shaman} is not adjacent to ${monolith.pos}")
            else -> nextState(boardState.copy(
                activeShamans = boardState.activeShamans - action.shaman,
                inactiveShamans = boardState.inactiveShamans + action.shaman.toInactiveShaman()
            ))
        }


    data class BanishShaman(val shaman: Shaman): Action

}


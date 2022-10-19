package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Monolith
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.states.*

class ActivatingDeerRock(
    override val boardState: BoardState,
    override val monolith: Monolith,
    override val shaman: Shaman,
    override val nextState: NextState
) : GameState, MonolithGameState {

    init {
        require(isValid(MonolithType.DeerRock))
    }

    override fun canActivate(): Boolean {
        return boardState.activeShamans.filter { it.pos.isAdjacent(monolith.pos) }
            .any { adjacentShaman ->
                adjacentShaman.pos.allAdjacent()
                    .any { shamanAdjacentPos ->
                        boardState.isFree(shamanAdjacentPos) && (boardState.board.isOnBoard(
                            shamanAdjacentPos
                        ) || boardState.board.isInVillage(
                            shamanAdjacentPos,
                            adjacentShaman.team.other()
                        ))
                    }
            }
    }

    override fun perform(action: Action): ActionResult = when (action) {
        is MoveShaman -> moveShaman(action)
        else -> ActionResult.InvalidAction(this, action)
    }

    private fun moveShaman(action: MoveShaman): ActionResult = when {
        !action.shaman.pos.isAdjacent(monolith.pos) -> ActionResult.InvalidAction("the selected shaman ${action.shaman} is not adjacent to the monolith $monolith")
        action.shaman !in boardState.activeShamans -> ActionResult.InvalidAction("the selected shaman ${action.shaman} is not an active shaman")
        !action.shaman.pos.isAdjacent(action.to) -> ActionResult.InvalidAction("the new position ${action.to} is not adjacent to the the selected shaman ${action.shaman}")
        !boardState.isFree(action.to) -> ActionResult.InvalidAction("the selected position is already occupied by ${boardState.shamanAt(action.to)}")
        else ->
            tryActivatingMonolith(
                action.to,
                nextState,
                boardState.copy(
                    activeShamans = boardState.activeShamans - action.shaman + action.shaman.copy(pos = action.to),
                )
            )
    }


    data class MoveShaman(val shaman: Shaman, val to: Pos) : Action
}
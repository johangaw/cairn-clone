package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.states.*

class ActivatingMenhirOfTheDancers(
    override val boardState: BoardState,
    override val monolith: Monolith,
    override val shaman: Shaman,
    override val nextState: NextState
) : GameState, MonolithGameState {

    init {
        require(isValid(MonolithType.MenhirOfTheDancers))
    }

    override fun canActivate(): Boolean {
        return shaman.pos.allAdjacent().filter {
            boardState.board.isOnBoard(it) || boardState.board.isInVillage(
                it,
                shaman.team.other()
            )
        }.any(boardState::isFree)
    }

    override fun perform(action: Action): ActionResult {
        return when (action) {
            is MoveShaman -> moveShaman(action)
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun moveShaman(action: MoveShaman): ActionResult {
        return when {
            !monolith.pos.isAdjacent(action.pos) -> ActionResult.InvalidAction("the selected position ${action.pos} is not adjacent to the monolith $monolith")
            !boardState.isFree(action.pos) -> ActionResult.InvalidAction("the selected position ${action.pos} is already occupied")
            !boardState.board.isOnBoard(action.pos) && !boardState.board.isInVillage(
                action.pos,
                shaman.team.other()
            ) -> ActionResult.InvalidAction("the selected ${action.pos} position is not valid")
            else -> tryActivatingMonolith(
                action.pos,
                nextState,
                boardState.copy(
                    activeShamans = (if (boardState.board.isInVillage(
                            action.pos,
                            shaman.team.other()
                        )
                    ) boardState.activeShamans else boardState.activeShamans + shaman.copy(pos = action.pos)) - shaman

                )
            )
        }
    }

    data class MoveShaman(val pos: Pos) : Action
}
package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Monolith
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState
import com.example.cairnclone.game.states.tryActivatingMonolith

class ActivatingCromlechOfTheStars(
    override val boardState: BoardState,
    val shaman: Shaman,
    val current: Monolith,
    val nextState: (boardState: BoardState) -> ActionResult.NewState
) : GameState, MonolithGameState {

    init {
        require(current in boardState.activeMonoliths) { "the monolith it not active" }
        require(current.type == MonolithType.CromlechOfTheStars) { "the monolith it of wrong type ${current.type}" }
        require(shaman in boardState.activeShamans) { "the shaman is not active" }
        require(shaman.pos == current.pos) { "the shaman is not located on the monolith" }
    }

    override val monolith: MonolithType = MonolithType.CromlechOfTheStars

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
            else -> tryActivatingMonolith(action.monolith.pos, shaman.team, nextState, boardState.copy(
                activeShamans = boardState.activeShamans - shaman + shaman.copy(pos = action.monolith.pos)
            ))
        }
    }


    data class MoveToMonolith(val monolith: Monolith) : Action
}
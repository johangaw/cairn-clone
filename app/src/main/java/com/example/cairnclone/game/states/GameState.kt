package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.BoardState

typealias NextState = (boardState: BoardState) -> ActionResult.NewState

interface GameState {
    val boardState: BoardState
    fun perform(action: Action): ActionResult
}

sealed class ActionResult {
    data class InvalidAction(val msg: String) : ActionResult() {
        constructor(
            state: GameState,
            action: Action
        ) : this("${action.javaClass.simpleName} not allowed on ${state.javaClass.simpleName}")
    }

    data class NothingToDo(val preloadActions: List<Action> = emptyList()) : ActionResult()
    data class NewState(val state: GameState, val preloadActions: List<Action> = emptyList()) :
        ActionResult()
}

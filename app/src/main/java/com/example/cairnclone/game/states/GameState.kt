package com.example.cairnclone.game.states

import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.actions.Action

abstract class GameState(
    val boardState: BoardState
) {
    abstract fun perform(action: Action): ActionResult
}

sealed class ActionResult {
    data class InvalidAction(val msg: String): ActionResult() {
        constructor(state: GameState, action: Action): this("${action.javaClass.simpleName} not allowed on ${state.javaClass.simpleName}")
    }
    object NothingToDo: ActionResult()
    data class NewState(val state: GameState, val preloadActions: List<Action> = listOf()): ActionResult()
}

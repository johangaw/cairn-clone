package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.actions.Action

abstract class GameState(
    val boardState: BoardState
) {
    abstract fun perform(action: Action): ActionResult
}

sealed class ActionResult {
    data class InvalidAction(val msg: String): ActionResult()
    object NothingToDo: ActionResult()
    data class NewState(val state: GameState, val preloadActions: List<Action> = listOf()): ActionResult()
}

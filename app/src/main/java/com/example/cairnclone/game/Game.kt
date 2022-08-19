package com.example.cairnclone.game

import android.util.Log
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState

class Game(
    private var _gameState: GameState,
    private val onGameStateChange: (gameState: GameState) -> Unit = {}
) {
    val LOG_TAG = Game::javaClass.name
    val gameState: GameState
        get() = _gameState

    fun perform(vararg actions: Action): Boolean {
        if (actions.isEmpty()) return true

        val headAction = actions.first()
        val tailActions = actions.drop(1)
        return when (val result = _gameState.perform(headAction)) {
            is ActionResult.InvalidAction -> {
                Log.d(LOG_TAG, "InvalidAction: ${result.msg}")
                false
            }
            is ActionResult.NewState -> {
                Log.d(LOG_TAG, "NewState: ${result.state.javaClass.simpleName}")
                _gameState = result.state
                onGameStateChange(_gameState)
                perform(*(result.preloadActions + tailActions).toTypedArray())
            }
            is ActionResult.NothingToDo -> perform(*(result.preloadActions + tailActions).toTypedArray())
        }
    }
}
package com.example.cairnclone.game

import android.util.Log
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState

class Game(
    private var _gameState: GameState
){
    val LOG_TAG = Game::javaClass.name
    val gameState: GameState
        get() = _gameState

    private var upcomingActions: List<Action> = listOf()

    fun perform(vararg actions: Action): Boolean {
        if(actions.isEmpty()) return true

        val headAction = actions.first()
        val tailActions = actions.drop(1)
        return when(val result = gameState.perform(headAction)) {
            is ActionResult.InvalidAction -> {
                Log.d(LOG_TAG, "InvalidAction: ${result.msg}")
                false
            }
            is ActionResult.NewState -> {
                _gameState = result.state
                perform(*(tailActions + result.preloadActions).toTypedArray())
            }
            is ActionResult.NothingToDo -> true
        }
    }
}
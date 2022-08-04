package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.SpawnShaman

class WaitForAction(boardState: BoardState) : GameState(boardState) {

    override fun perform(action: Action): ActionResult {
        return when (action) {
            is SpawnShaman -> validateSpawnShaman(action)
            else -> ActionResult.NewState(this, listOf())
        }
    }

    private fun validateSpawnShaman(action: SpawnShaman): ActionResult {
        return when {
            boardState.activeTeam != action.shaman.team -> ActionResult.InvalidAction("a shaman for team ${action.shaman.team} can not be spawn right now")
            !boardState.inactiveShamans.contains(action.shaman) -> ActionResult.InvalidAction("the selected shaman ${action.shaman.id} is not inactive")
            boardState.activeShamanAt(action.shaman.pos)?.team == action.shaman.team -> ActionResult.InvalidAction("the selected span location (${action.shaman.pos}) already contains a shaman from the same team")
            else -> ActionResult.NewState(Spawning(boardState), listOf(action))
        }
    }
}
package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.MoveShaman
import com.example.cairnclone.game.actions.SpawnShaman
import com.example.cairnclone.game.other

class WaitForAction(boardState: BoardState) : GameState(boardState) {

    override fun perform(action: Action): ActionResult {
        return when (action) {
            is SpawnShaman -> validateSpawnShaman(action)
            is MoveShaman -> validateMoveShaman(action)
            else -> ActionResult.InvalidAction("${action.javaClass.name} not allowed on ${this.javaClass.name}")
        }
    }

    private fun validateSpawnShaman(action: SpawnShaman): ActionResult {
        return when {
            boardState.activeTeam != action.shaman.team -> ActionResult.InvalidAction("a shaman for team ${action.shaman.team} can not be spawn right now")
            !boardState.inactiveShamans.contains(action.shaman) -> ActionResult.InvalidAction("the selected shaman ${action.shaman.id} is not inactive")
            boardState.shamanAt(action.shaman.pos)?.team == action.shaman.team -> ActionResult.InvalidAction("the selected span location (${action.shaman.pos}) already contains a shaman from the same team")
            else -> ActionResult.NewState(Spawning(boardState), listOf(action))
        }
    }

    private fun validateMoveShaman(action: MoveShaman): ActionResult {
        val shaman = boardState.activeShaman(action.shamanId)
        return when {
            boardState.activeTeam != action.team -> ActionResult.InvalidAction("the team ${action.team} is not allowed to move a shaman right now")
            shaman == null -> ActionResult.InvalidAction("the selected shaman ${action.shamanId} is not active")
            shaman.team != action.team -> ActionResult.InvalidAction("the selected shaman ${action.shamanId} is not part of the ${action.team} team")
            action.newPos.x !in -1..boardState.board.width -> ActionResult.InvalidAction("newPos ${action.newPos} is outside of board and villages")
            action.newPos.y !in -1..boardState.board.height -> ActionResult.InvalidAction("newPos ${action.newPos} is outside of board and villages")
            boardState.shamanAt(action.newPos) != null -> ActionResult.InvalidAction("${action.newPos} already contains a shaman")
            !boardState.moveActionTile.possibleTargets(shaman.pos).contains(action.newPos) -> ActionResult.InvalidAction("${boardState.moveActionTile} does not allow moving $shaman to ${action.newPos}")
            boardState.isInVillage(action.newPos, action.team) -> ActionResult.InvalidAction("shaman $shaman can't move into it's own village")
            else -> ActionResult.NewState(Moving(boardState), listOf(action))
        }
    }
}
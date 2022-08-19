package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.*

class ActivatingChaosOfTheGiants(boardState: BoardState, val team: Team, val nextState: (boardState: BoardState) -> ActionResult.NewState): GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when(action) {
            is Activate -> activate(action)
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun activate(action: Activate): ActionResult {
        return when {
            !boardState.activeShamans.contains(action.shamanToBanish) -> ActionResult.InvalidAction("the selected shaman ${action.shamanToBanish} is not an active shaman")
            action.shamanToBanish.team == team -> ActionResult.InvalidAction("can't banish a shaman from the same team")
            !boardState.board.isInFirstRow(action.shamanToBanish.pos, team) -> ActionResult.InvalidAction("the selection shaman ${action.shamanToBanish} is not in the first row of team $team")
            else -> nextState(boardState.copy(
                activeShamans = boardState.activeShamans - action.shamanToBanish,
                inactiveShamans = boardState.inactiveShamans + action.shamanToBanish.toInactiveShaman()
            ))
        }
    }

    data class Activate(val shamanToBanish: Shaman): Action
}

private fun Board.isInFirstRow(pos: Pos, team: Team): Boolean {
    return when(villageIndex[team]!!) {
        height ->  pos.y == height - 1
        -1 -> pos.y == 0
        else -> false
    }
}


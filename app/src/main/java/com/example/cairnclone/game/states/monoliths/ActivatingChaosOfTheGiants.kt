package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState

class ActivatingChaosOfTheGiants(
    boardState: BoardState,
    val team: Team,
    val nextState: (boardState: BoardState) -> ActionResult.NewState
) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is Activate -> activate(action)
            is Skipp -> skipp()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun skipp(): ActionResult =
        if (banishableShamans.isNotEmpty())
            ActionResult.InvalidAction("there are banishable shamans at ${banishableShamans.map { it.pos }}")
        else
            nextState(boardState)


    private fun activate(action: Activate): ActionResult =
        when {
            !boardState.activeShamans.contains(action.shamanToBanish) -> ActionResult.InvalidAction(
                "the selected shaman ${action.shamanToBanish} is not an active shaman"
            )
            action.shamanToBanish.team == team -> ActionResult.InvalidAction("can't banish a shaman from the same team")
            !boardState.board.isInFirstRow(
                action.shamanToBanish.pos,
                team
            ) -> ActionResult.InvalidAction("the selection shaman ${action.shamanToBanish} is not in the first row of team $team")
            !banishableShamans.contains(action.shamanToBanish) -> ActionResult.InvalidAction("the selected shaman ${action.shamanToBanish} is not banishable")
            else -> nextState(
                boardState.copy(
                    activeShamans = boardState.activeShamans - action.shamanToBanish,
                    inactiveShamans = boardState.inactiveShamans + action.shamanToBanish.toInactiveShaman()
                )
            )
        }

    val banishableShamans
        get() = boardState.board.firstRowFor(team)
            .mapNotNull { boardState.shamanAt(it) }
            .filter { it.team != team }

    data class Activate(val shamanToBanish: Shaman) : Action
    object Skipp : Action
}

private fun Board.isInFirstRow(pos: Pos, team: Team) = firstRowFor(team).contains(pos)


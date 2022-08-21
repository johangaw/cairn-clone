package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.GameState

class ActivatingChaosOfTheGiants(
    boardState: BoardState,
    val team: Team,
    val nextState: (boardState: BoardState) -> ActionResult.NewState
) : GameState(boardState), MonolithGameState {

    override val monolith: MonolithType = MonolithType.ChaosOfTheGiants

    override fun canActivate(): Boolean = banishableShamans.isNotEmpty()

    override fun perform(action: Action): ActionResult {
        return when (action) {
            is Activate -> activate(action)
            else -> ActionResult.InvalidAction(this, action)
        }
    }

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

    private val banishableShamans
        get() = boardState.board.firstRowFor(team)
            .mapNotNull { boardState.shamanAt(it) }
            .filter { it.team != team }

    data class Activate(val shamanToBanish: Shaman) : Action
}

private fun Board.isInFirstRow(pos: Pos, team: Team) = firstRowFor(team).contains(pos)

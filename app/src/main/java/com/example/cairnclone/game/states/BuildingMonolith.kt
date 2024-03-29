package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.SelectMonolith
import com.example.cairnclone.game.board.*

class BuildingMonolith(
    private val newMonolithPos: Pos,
    private val newMonolithTeam: Team,
    private val nextState: (boardState: BoardState) -> ActionResult.NewState,
    override val boardState: BoardState
) : GameState {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is SelectMonolith -> ActionResult.NewState(
                this, listOf(
                    AddUpcomingMonolith(newMonolithPos, action.monolithType),
                    ScoreTeam(newMonolithTeam),
                    CompleteBuildingMonolith,
                )
            )
            is AddUpcomingMonolith -> handleAddUpcomingMonolith(action)
            is ScoreTeam -> handleScoreTeam(action)
            is CompleteBuildingMonolith -> handleCompleteBuildingMonolith()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun handleAddUpcomingMonolith(action: AddUpcomingMonolith): ActionResult =
        ActionResult.NewState(
            BuildingMonolith(
                newMonolithPos,
                newMonolithTeam,
                nextState,
                boardState.copy(
                    activeMonoliths = boardState.activeMonoliths + Monolith(action.pos, action.monolithType),
                    monolithsStack = boardState.monolithsStack - action.monolithType
                )
            )
        )

    private fun handleScoreTeam(action: ScoreTeam): ActionResult = ActionResult.NewState(
        BuildingMonolith(
            newMonolithPos,
            newMonolithTeam,
            nextState,
            boardState.copy(
                scores = boardState.scores.increment(action.team)
            )
        )
    )

    private fun handleCompleteBuildingMonolith(): ActionResult = nextState(boardState)

    private data class AddUpcomingMonolith(val pos: Pos, val monolithType: MonolithType) : Action
    private data class ScoreTeam(val team: Team) : Action
    private object CompleteBuildingMonolith : Action
}

private fun Score.increment(): Score = Score(this.value + 1)

private fun Scores.increment(team: Team): Scores = when(team) {
    Team.Sea -> this.copy(seaTeam = seaTeam.increment())
    Team.Forest -> this.copy(seaTeam = forestTeam.increment())
}

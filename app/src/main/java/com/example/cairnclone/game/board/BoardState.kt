package com.example.cairnclone.game.board

import com.example.cairnclone.game.Monolith
import com.example.cairnclone.game.MonolithType

data class BoardState(
    val activeTeam: Team,
    val board: Board,
    val spawnActionTile: SpawnActionTile,
    val moveActionTile: MoveActionTile,
    val transformationTile: TransformationTile,
    val inactiveShamans: List<Shaman>,
    val activeShamans: List<Shaman>,
    val activeMonoliths: List<Monolith>,
    val monolithsStack: List<MonolithType>,
    val scores: Scores
) {
    fun shamanAt(pos: Pos): Shaman? = activeShamans.find { it.pos == pos }
    fun isInVillage(pos: Pos, team: Team) = board.villageIndex[team] == pos.y
    fun monolithAt(pos: Pos) = activeMonoliths.find { it.pos == pos }
    val upcomingMonoliths get() = monolithsStack.take(2)
}



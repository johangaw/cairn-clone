package com.example.cairnclone.game

import java.util.*

data class BoardState(
    val activeTeam: Team,
    val board: Board,
    val spawnActionTile: SpawnActionTile,
    val moveActionTile: MoveActionTile,
    val transformationTile: TransformationTile,
    val inactiveShamans: List<Shaman>,
    val activeShamans: List<Shaman>,
    val activeMonoliths: List<Monolith>,
    val upcomingMonoliths: List<MonolithType>,
    val monolithsStack: List<MonolithType>,
    val scores: Scores
) {
    fun shamanAt(pos: Pos): Shaman? = activeShamans.find { it.pos == pos }
    fun isInVillage(pos: Pos, team: Team) = board.villageIndex[team] == pos.y
    fun monolithAt(pos: Pos) = activeMonoliths.find { it.pos == pos }
}

data class Board(
    val width: Int = 5,
    val height: Int = 5,
    val villageIndex: Map<Team, Int> = mapOf(
        Team.Forest to -1,
        Team.Sea to height
    )
)

enum class SpawnActionTile(val positions: List<Pos>) {
    SpawnWhite(listOf(Pos(1, 0), Pos(3, 4))),
    SpawnBlack(listOf(Pos(3, 0), Pos(1, 4)))
}

sealed class MoveActionTile(private val moveDirections: List<Direction>) {
    object Orthogonally :
        MoveActionTile(listOf(Direction.Up, Direction.Down, Direction.Left, Direction.Right))

    object Diagonally : MoveActionTile(
        listOf(
            Direction.UpLeft,
            Direction.UpRight,
            Direction.DownLeft,
            Direction.DownRight
        )
    )

    fun possibleTargets(from: Pos): List<Pos> = moveDirections.map { from + it }
}

sealed class TransformationTile {
    object Surrounded : TransformationTile() {
        override fun isApplicable(pos1: Pos, pos2: Pos, target: Pos): Boolean {
            return pos1.adjacentDirection(target)?.let { target + it == pos2 } ?: false
        }
    }

    object Outnumbered : TransformationTile() {
        override fun isApplicable(pos1: Pos, pos2: Pos, target: Pos): Boolean {
            return pos1.adjacentDirection(pos2)?.let { pos2 + it == target } ?: false
                    || pos2.adjacentDirection(pos1)?.let { pos1 + it == target } ?: false
        }
    }

    abstract fun isApplicable(pos1: Pos, pos2: Pos, target: Pos): Boolean
}

enum class Team {
    Forest,
    Sea,
}

fun Team.other(): Team = when (this) {
    Team.Forest -> Team.Sea
    Team.Sea -> Team.Forest
}

data class Pos(
    val x: Int,
    val y: Int,
)

operator fun Pos.plus(dir: Direction): Pos {
    return this.copy(x = this.x + dir.dx, y = this.y + dir.dy)
}

fun Pos.adjacentDirection(other: Pos): Direction? {
    val dx = other.x - x
    val dy = other.y - y

    return Direction.values().find { it.dx == dx && it.dy == dy }
}

enum class Direction(val dx: Int, val dy: Int) {
    Up(0, -1),
    Down(0, 1),
    Right(1, 0),
    Left(-1, 0),

    UpRight(1, -1),
    UpLeft(-1, -1),
    DownRight(1, 1),
    DownLeft(-1, 1)
}

data class Scores(val seaTeam: Score, val forestTeam: Score)

@JvmInline
value class Score(val value: Int) {
    init {
        require(value in 0..3)
    }
}

@JvmInline
value class ShamanId(private val id: UUID = UUID.randomUUID())

data class Shaman(
    val id: ShamanId = ShamanId(),
    val team: Team,
    val pos: Pos,
)
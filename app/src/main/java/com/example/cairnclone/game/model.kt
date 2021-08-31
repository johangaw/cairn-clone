package com.example.cairnclone.game

import kotlin.random.Random

data class Game(
    val board: Board = Board(),
    val shamans: Set<Shaman> = setOf(),
    val monoliths: Set<Monolith> = setOf(),
    val actions: List<Action> = listOf(),
    val transformation: Transformation = Transformation.Outnumbered,
    val activeTeam: Team = Team.Forest,
)

data class Monolith(
    val pos: Pos,
    val power: MonolithPower
)

enum class MonolithPower {
    MoveShamanAgain
}

data class Board(
    val width: Int = 5,
    val height: Int = 5,
    val whiteSpawn: Map<Team, Pos> = mapOf(
        Team.Sea to Pos(1, 0),
        Team.Forest to Pos(3, height - 1)
    ),
    val blackSpawn: Map<Team, Pos> = mapOf(
        Team.Sea to Pos(3, 0),
        Team.Forest to Pos(1, height - 1)
    ),
)

enum class Team {
    Forest,
    Sea,
}

enum class Transformation {
    Surrounded,
    Outnumbered,
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

data class Shaman(
    val id: Long = Random.nextLong(),
    val team: Team,
    val pos: Pos,
)

enum class Action {
    MoveShamanDiagonally,
    MoveShamanOrthogonally,
    SpawnShamanOnWhite,
    SpawnShamanOnBlack
}
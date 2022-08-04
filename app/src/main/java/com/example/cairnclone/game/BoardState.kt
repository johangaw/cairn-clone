package com.example.cairnclone.game

import java.util.*

data class BoardState(
    val activeTeam: Team,
    val board: Board,
    val spawnActionTile: SpawnActionTile,
    val inactiveShamans: List<Shaman>,
    val activeShamans: List<Shaman>
) {
    fun activeShamanAt(pos: Pos): Shaman? = activeShamans.find { it.pos == pos }
}

data class Board(
    val width: Int = 5,
    val height: Int = 5,
)

//data class Monolith(
//    val id: Long = Random.nextLong(),
//    val pos: Pos,
//    val power: MonolithPower,
//)
//
//enum class MonolithPower {
//    MoveShamanAgain
//}

enum class SpawnActionTile(val positions: List<Pos>) {
    SpawnWhite(listOf(Pos(0, 1), Pos(4, 3))),
    SpawnBlack(listOf(Pos(0, 3), Pos(4, 1)))
}

enum class Team {
    Forest,
    Sea,
}

//enum class Transformation {
//    Surrounded,
//    Outnumbered,
//}

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
    val id: UUID = UUID.randomUUID(),
    val team: Team,
    val pos: Pos,
) {
    override fun equals(other: Any?) = other != null && other is Shaman && other.id == id
    override fun hashCode(): Int = id.hashCode()
}
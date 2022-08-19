package com.example.cairnclone.game.board

data class Board(
    val width: Int = 5,
    val height: Int = 5,
    val villageIndex: Map<Team, Int> = mapOf(
        Team.Forest to -1,
        Team.Sea to height
    ),
    val forestVillageRow: List<Pos> = (-1..width).map { Pos(it, -1) },
    val seaVillageRow: List<Pos> = (-1..width).map { Pos(it, height) },

    val forestFirstRow: List<Pos> = (0 until width).map { Pos(it, 0) },
    val seaFirstRow: List<Pos> = (0 until width).map { Pos(it, height - 1) },
) {
    fun villageRowFor(team: Team) = when(team) {
        Team.Forest -> forestVillageRow
        Team.Sea -> seaVillageRow
    }

    fun firstRowFor(team: Team) = when(team) {
        Team.Forest -> forestFirstRow
        Team.Sea -> seaFirstRow
    }
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
package com.example.cairnclone.game

import kotlin.random.Random

data class Game(
    val board: Board = Board(),
    val shamans: Set<Shaman> = setOf(),
    val actions: List<Action> = listOf(),
    val activeTeam: Team = Team.Forest,

    /* TODO: change game interaction model
        Create channel of events by which the UI should interact with the game
        Instead of calling methods directly the UI should only dispatch events
        Wrap Game in a state machine which listens to the event stream
        If the event is relevant for the current state, then update game state
     */

)

data class Board(
    val width: Int = 5,
    val height: Int = 5,
)

enum class Team {
    Forest,
    Sea,
}

data class Pos(
    val x: Int,
    val y: Int,
)

operator fun Pos.plus(dir: Direction): Pos {
    return this.copy(x = this.x + dir.dx, y = this.y + dir.dy)
}

enum class Direction(val dx: Int, val dy: Int) {
    Up(0, -1),
    Down(0, 1),
    Right(1, 0),
    Left(-1, 0),

    UpRight(1,-1),
    UpLeft(-1, -1),
    DownRight(1,1),
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
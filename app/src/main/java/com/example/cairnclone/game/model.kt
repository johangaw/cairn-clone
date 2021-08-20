package com.example.cairnclone.game

import kotlin.random.Random

data class Game(
    val board: Board = Board(),
    val shamans: Set<Shaman> = setOf()
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

data class Shaman(
    val id: Long = Random.nextLong(),
    val team: Team,
    val pos: Pos,
)
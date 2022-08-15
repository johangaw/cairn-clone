package com.example.cairnclone.game.board

enum class Team {
    Forest,
    Sea,
}

fun Team.other(): Team = when (this) {
    Team.Forest -> Team.Sea
    Team.Sea -> Team.Forest
}

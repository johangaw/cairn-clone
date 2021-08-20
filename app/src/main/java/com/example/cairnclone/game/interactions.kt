package com.example.cairnclone.game

fun Game.spawnShaman(team: Team, pos: Pos): Game {
    val posTaken = this.shamans.any { it.pos == pos }
    return when {
        posTaken -> this
        else -> this.copy(shamans = this.shamans + Shaman(team, pos))
    }
}

fun Game.banishShaman(shaman: Shaman): Game {
    return this.copy(shamans = this.shamans - shaman)
}

fun Game.move(shaman: Shaman, pos: Pos): Game {
    val posTaken = this.shamans.any { it.pos == pos }
    return when {
        posTaken -> this
        else -> this.copy(shamans = this.shamans - shaman + shaman.copy(pos = pos))
    }
}

//fun Game.move(shaman: Shaman, pos: Pos): Game {
//
//}
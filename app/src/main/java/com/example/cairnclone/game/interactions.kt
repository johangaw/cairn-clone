package com.example.cairnclone.game

fun Game.spawnShaman(team: Team, pos: Pos): Game {
    val posTaken = this.shamans.any { it.pos == pos }
    return when {
        posTaken -> this
        else -> this.copy(shamans = this.shamans + Shaman(team = team, pos = pos))
    }
}

fun Game.banishShaman(shaman: Shaman): Game {
    return this.copy(shamans = this.shamans - shaman)
}

fun Game.move(shaman: Shaman, pos: Pos): Game {
    val action =
        this.possibleMoves(shaman).entries.firstOrNull { (_, positions) -> positions.contains(pos) }
            ?.component1() ?: return this

    val posTaken = this.shamans.any { it.pos == pos }
    return when {
        posTaken -> this
        else -> this.copy(
            shamans = this.shamans - shaman + shaman.copy(pos = pos),
            actions = actions.map { if (it == action) action.flip() else it })
    }
}

fun Action.flip(): Action {
    return when (this) {
        Action.MoveShamanDiagonally -> Action.MoveShamanOrthogonally
        Action.MoveShamanOrthogonally -> Action.MoveShamanDiagonally
    }
}

//fun Game.move(shaman: Shaman, pos: Pos): Game {
//
//}
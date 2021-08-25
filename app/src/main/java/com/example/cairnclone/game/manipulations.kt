package com.example.cairnclone.game

fun Game.spawnShaman(team: Team, pos: Pos): Game {
    val action = possibleSpawnAction(pos) ?: return this

    val shamanAtPos = this.shamans.firstOrNull { it.pos == pos }
    return when (team) {
        shamanAtPos?.team -> this
        else -> this.copy(
            shamans = if (shamanAtPos != null) this.shamans - shamanAtPos else this.shamans + Shaman(
                team = team,
                pos = pos
            ),
            actions = actions.flip(action)
        )
    }
}

fun Game.endTurn(): Game {
    return this.copy(
        activeTeam = when (activeTeam) {
            Team.Forest -> Team.Sea
            Team.Sea -> Team.Forest
        }
    )
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
            actions = actions.flip(action)
        )
    }
}

fun Action.flip(): Action {
    return when (this) {
        Action.MoveShamanDiagonally -> Action.MoveShamanOrthogonally
        Action.MoveShamanOrthogonally -> Action.MoveShamanDiagonally
        Action.SpawnShamanOnBlack -> Action.SpawnShamanOnWhite
        Action.SpawnShamanOnWhite -> Action.SpawnShamanOnBlack
    }
}

fun List<Action>.flip(action: Action): List<Action> =
    this.map { if (it == action) action.flip() else it }

//fun Game.move(shaman: Shaman, pos: Pos): Game {
//
//}
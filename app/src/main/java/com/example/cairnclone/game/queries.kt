package com.example.cairnclone.game

fun Game.shamanAt(pos: Pos): Shaman? {
    return this.shamans.firstOrNull { it.pos == pos }
}

fun Game.monolithAt(pos: Pos): Monolith? {
    return this.monoliths.firstOrNull { it.pos == pos }
}

fun  Game.possibleTransformation(s1: Shaman, s2: Shaman, enemyShaman: Shaman): Transformation? {
    return when {
        s1.team != s2.team -> null
        s1.team == enemyShaman.team -> null
        transformation == Transformation.Surrounded -> {
            val dir = s1.pos.adjacentDirection(enemyShaman.pos) ?: return null
            return if(enemyShaman.pos + dir == s2.pos) Transformation.Surrounded else null
        }
        transformation == Transformation.Outnumbered -> {
            return enemyShaman.pos.adjacentDirection(s1.pos)?.let { if(s1.pos + it == s2.pos) Transformation.Outnumbered else null }
                ?: enemyShaman.pos.adjacentDirection(s2.pos)?.let { if(s2.pos + it == s1.pos) Transformation.Outnumbered else null }
        }
        else -> return null
    }
}

fun Game.possibleSpawnAction(pos: Pos): Action? {
    return when (pos) {
        board.whiteSpawn[activeTeam] -> actions.first { it == Action.SpawnShamanOnWhite }
        board.blackSpawn[activeTeam] -> actions.first { it == Action.SpawnShamanOnBlack }
        else -> null
    }
}

fun Game.possibleMoves(shaman: Shaman): Map<Action, List<Pos>> {
    return this.actions.map { it to this.possibleMoves(shaman, it) }.toMap()
}

fun Game.possibleMoves(shaman: Shaman, action: Action): List<Pos> {
    return when (action) {
        Action.MoveShamanOrthogonally -> listOf(
            Direction.Up,
            Direction.Down,
            Direction.Left,
            Direction.Right
        ).map { shaman.pos + it }
        Action.MoveShamanDiagonally -> listOf(
            Direction.UpRight,
            Direction.UpLeft,
            Direction.DownRight,
            Direction.DownLeft
        ).map { shaman.pos + it }
        else -> listOf()
    }
}

fun Game.shamanAt(x: Int, y: Int): Shaman? = this.shamanAt(Pos(x, y))
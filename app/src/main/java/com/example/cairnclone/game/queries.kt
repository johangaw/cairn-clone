package com.example.cairnclone.game

fun Game.shamanAt(pos: Pos): Shaman? {
    return this.shamans.firstOrNull { it.pos == pos }
}

fun Game.possibleMoves(shaman: Shaman): Map<Action, List<Pos>> {
    return this.actions.map { it to this.possibleMoves(shaman, it) }.toMap()
}

fun Game.possibleMoves(shaman: Shaman, action: Action): List<Pos> {
    return when(action) {
        Action.MoveShamanOrthogonally -> listOf(Direction.Up, Direction.Down, Direction.Left, Direction.Right).map { shaman.pos + it }
        Action.MoveShamanDiagonally -> listOf(Direction.UpRight, Direction.UpLeft, Direction.DownRight, Direction.DownLeft).map { shaman.pos + it }
        else -> listOf()
    }
}

fun Game.shamanAt(x: Int, y: Int): Shaman? = this.shamanAt(Pos(x,y))
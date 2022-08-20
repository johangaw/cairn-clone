package com.example.cairnclone.game.board

import com.example.cairnclone.game.states.Jumping

enum class SpawnActionTile(val forest: Pos, val sea: Pos) {
    White(forest = Pos(1, 0), sea = Pos(3, 4)),
    Black(forest = Pos(3, 0), sea = Pos(1, 4)),
}

fun SpawnActionTile.flip(): SpawnActionTile = when (this) {
    SpawnActionTile.White -> SpawnActionTile.Black
    SpawnActionTile.Black -> SpawnActionTile.White
}

fun SpawnActionTile.posFor(team: Team): Pos = when(team) {
    Team.Forest -> this.forest
    Team.Sea -> this.sea
}

sealed class MoveActionTile(private val moveDirections: List<Direction>) {
    object Orthogonally :
        MoveActionTile(listOf(Direction.Up, Direction.Down, Direction.Left, Direction.Right))

    object Diagonally : MoveActionTile(
        listOf(
            Direction.UpLeft,
            Direction.UpRight,
            Direction.DownLeft,
            Direction.DownRight
        )
    )

    fun flip(): MoveActionTile = when (this) {
        is Diagonally -> Orthogonally
        is Orthogonally -> Diagonally
    }

    fun possibleTargets(from: Pos): List<Pos> = moveDirections.map { from + it }
}

sealed class JumpActionTile {
    object OverTeamMate: JumpActionTile() {
        override fun isApplicable(jumper: Team, springboard: Team): Boolean =
            jumper == springboard
    }
    object OverOpponent: JumpActionTile() {
        override fun isApplicable(jumper: Team, springboard: Team): Boolean =
            jumper != springboard
    }

    abstract fun isApplicable(jumper: Team, springboard: Team): Boolean

    fun flip() = when(this) {
        OverOpponent -> OverTeamMate
        OverTeamMate -> OverOpponent
    }
}

sealed class TransformationTile {
    object Surrounded : TransformationTile() {
        override fun isApplicable(pos1: Pos, pos2: Pos, target: Pos): Boolean {
            return pos1.adjacentDirection(target)?.let { target + it == pos2 } ?: false
        }
    }

    object Outnumbered : TransformationTile() {
        override fun isApplicable(pos1: Pos, pos2: Pos, target: Pos): Boolean {
            return pos1.adjacentDirection(pos2)?.let { pos2 + it == target } ?: false
                    || pos2.adjacentDirection(pos1)?.let { pos1 + it == target } ?: false
        }
    }

    fun flip(): TransformationTile =
        when (this) {
            is Outnumbered -> Surrounded
            is Surrounded -> Outnumbered
        }

    abstract fun isApplicable(pos1: Pos, pos2: Pos, target: Pos): Boolean
}
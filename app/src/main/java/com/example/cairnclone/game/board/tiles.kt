package com.example.cairnclone.game.board

enum class SpawnActionTile(val positions: List<Pos>) {
    SpawnWhite(listOf(Pos(1, 0), Pos(3, 4))),
    SpawnBlack(listOf(Pos(3, 0), Pos(1, 4))),
}

fun SpawnActionTile.flip(): SpawnActionTile = when (this) {
    SpawnActionTile.SpawnWhite -> SpawnActionTile.SpawnBlack
    SpawnActionTile.SpawnBlack -> SpawnActionTile.SpawnWhite
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
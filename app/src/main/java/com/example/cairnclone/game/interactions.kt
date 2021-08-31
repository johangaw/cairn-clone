package com.example.cairnclone.game


abstract class GameState(val game: Game) {
    abstract fun interact(interaction: Interaction): GameState
}

class WaitingForAction(game: Game) : GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return when (interaction) {
            is MoveShaman -> {
                val (shaman, pos) = interaction
                game.move(shaman, pos).let {
                    if (it != game) WaitingForTransformationOrEndOfTurn(it) else this
                }
            }
            is SpawnShaman -> {
                val (team, pos) = interaction
                game.spawnShaman(team, pos).let {
                    if (it != game) WaitingForTransformationOrEndOfTurn(it) else this
                }
            }
            is EndTurn -> {
                WaitingForAction(game.endTurn())
            }
            else -> this
        }
    }
}

class WaitingForTransformationOrEndOfTurn(game: Game) : GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return when (interaction) {
            is TransformShamans -> {
                val (shaman1, shaman2, enemyShaman) = interaction
                val newGame = game.transformShamans(shaman1, shaman2, enemyShaman)

                if (newGame == game) return this

                newGame.monolithAt(enemyShaman.pos)?.let { WaitingForEndOfTurn(newGame) }
                    ?: WaitForSelectingMonolithToSpawn(newGame, shaman1.team)

            }
            is EndTurn -> {
                WaitingForAction(game.endTurn())
            }
            else -> this
        }
    }
}

class WaitForSelectingMonolithToSpawn(game: Game, val activeTeam: Team) : GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return this
    }
}

class WaitingForEndOfTurn(game: Game) : GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return when (interaction) {
            is EndTurn -> {
                WaitingForAction(game.endTurn())
            }
            else -> this
        }
    }
}

interface Interaction {}

data class MoveShaman(val shaman: Shaman, val pos: Pos) : Interaction

data class SpawnShaman(val team: Team, val pos: Pos) : Interaction

data class TransformShamans(
    val shamanFriend1: Shaman,
    val shamanFriend2: Shaman,
    val shamanEnemy: Shaman
) : Interaction

class EndTurn() : Interaction

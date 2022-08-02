package com.example.cairnclone.game_old


abstract class GameState(val game: Game) {
    abstract fun interact(interaction: Interaction): GameState
}

class SelectAnAction(game: Game) : GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return when (interaction) {
            is MoveShaman -> {
                val (shaman, pos) = interaction
                game.moveWithAction(shaman, pos).let { newGame ->
                    if (newGame != game) {
                        newGame.monolithAt(pos)?.let {
                            ActivateMonolith(newGame, it, shaman.team)
                        } ?: SelectATransformationOrEndOfTurn(newGame)
                    } else {
                        this
                    }
                }
            }
            is SpawnShaman -> {
                val (team, pos) = interaction
                game.spawnShaman(team, pos).let {
                    if (it != game) SelectATransformationOrEndOfTurn(it) else this
                }
            }
            is EndTurn -> {
                SelectAnAction(game.endTurn())
            }
            else -> this
        }
    }
}

class SelectATransformationOrEndOfTurn(game: Game) : GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return when (interaction) {
            is TransformShamans -> {
                val (shaman1, shaman2, enemyShaman) = interaction
                val newGame = game.transformShamans(shaman1, shaman2, enemyShaman)

                if (newGame == game) return this

                newGame.monolithAt(enemyShaman.pos)?.let { SelectEndOfTurn(newGame) }
                    ?: SelectingMonolithToSpawn(newGame, shaman1.team)
            }
            is EndTurn -> {
                SelectAnAction(game.endTurn())
            }
            else -> this
        }
    }
}

class SelectingMonolithToSpawn(game: Game, private val team: Team) : GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return when (interaction) {
            is SpawnMonolith -> {
                val (power, pos) = interaction
                val newGame = game.spawnMonolith(power, team, pos)

                if(newGame != game) return this

                return SelectEndOfTurn(newGame)
            }
            else -> this
        }
    }
}

class ActivateMonolith(game: Game, val monolith: Monolith, val team: Team): GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return when {
             monolith.power == MonolithPower.MoveShamanAgain && interaction is MoveShaman -> {
                 val (shaman, pos) = interaction

                 if(shaman.pos != monolith.pos) return this
                 if(shaman.team != team) return this
                 if(monolith.pos.adjacentDirection(pos) == null) return this

                 return game.moveWithAction(shaman, pos).let { newGame ->
                     if (newGame != game) {
                         newGame.monolithAt(pos)?.let {
                             ActivateMonolith(newGame, it, shaman.team)
                         } ?: SelectATransformationOrEndOfTurn(newGame)
                     } else {
                         this
                     }
                 }
             }
            else -> this
        }
    }

}

class SelectEndOfTurn(game: Game) : GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return when (interaction) {
            is EndTurn -> SelectAnAction(game.endTurn())
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

data class SpawnMonolith(
    val power: MonolithPower,
    val pos: Pos
) : Interaction

class EndTurn() : Interaction

package com.example.cairnclone.game


abstract class GameState(val game: Game) {
    abstract fun interact(interaction: Interaction): GameState
}

class WaitingForAction(game: Game) : GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return when(interaction) {
            is MoveShaman -> {
                val (shaman, pos) = interaction
                game.move(shaman, pos).let {
                    if(it != game) WaitingForTransformationOrEndOfTurn(it) else this
                }
            }
            is SpawnShaman -> {
                val (team, pos) = interaction
                game.spawnShaman(team, pos).let {
                    if(it != game) WaitingForTransformationOrEndOfTurn(it) else this
                }
            }
            is EndTurn -> {
                WaitingForAction(game.endTurn())
            }
            else -> this
        }
    }
}

class WaitingForTransformationOrEndOfTurn(game: Game): GameState(game) {
    override fun interact(interaction: Interaction): GameState {
        return when(interaction) {
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

class EndTurn() : Interaction

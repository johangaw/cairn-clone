package com.example.cairnclone.game.states

import com.example.cairnclone.game.BoardState
import com.example.cairnclone.game.Pos
import com.example.cairnclone.game.Shaman
import com.example.cairnclone.game.SpawnActionTile
import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.SpawnShaman

class Spawning(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is SpawnShaman -> ActionResult.NewState(
                this, listOf(
                    RemoveShaman(action.shaman.pos),
                    AddShaman(action.shaman),
                    FlipSpawnTile,
                    CompleteSpawning,
                )
            )
            is RemoveShaman -> removeShaman(action)
            is AddShaman -> addShaman(action)
            is FlipSpawnTile -> flipActionTile()
            is CompleteSpawning -> completeSpawning()
            else -> ActionResult.InvalidAction("${action.javaClass.name} not allowed on ${this.javaClass.name}")
        }
    }

    private fun removeShaman(action: RemoveShaman): ActionResult =
        boardState.shamanAt(action.pos)?.let {
            ActionResult.NewState(
                Spawning(
                    boardState.copy(
                        inactiveShamans = boardState.inactiveShamans + it,
                        activeShamans = boardState.activeShamans - it
                    )
                )
            )
        } ?: ActionResult.NothingToDo


    private fun addShaman(action: AddShaman): ActionResult =
        ActionResult.NewState(
            Spawning(
                boardState.copy(
                    inactiveShamans = boardState.inactiveShamans - action.shaman,
                    activeShamans = boardState.activeShamans + action.shaman
                )
            )
        )

    private fun flipActionTile(): ActionResult = ActionResult.NewState(
        Spawning(
            boardState.copy(
                spawnActionTile = boardState.spawnActionTile.flip()
            )
        )
    )

    private fun completeSpawning(): ActionResult =
        ActionResult.NewState(ActivatingMonolith(boardState))

}

private fun SpawnActionTile.flip(): SpawnActionTile = when (this) {
    SpawnActionTile.SpawnWhite -> SpawnActionTile.SpawnBlack
    SpawnActionTile.SpawnBlack -> SpawnActionTile.SpawnWhite
}

private data class RemoveShaman(val pos: Pos) : Action
private data class AddShaman(val shaman: Shaman) : Action
private object FlipSpawnTile : Action
private object CompleteSpawning : Action
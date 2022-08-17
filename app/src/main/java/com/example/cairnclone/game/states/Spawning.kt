package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.SpawnShaman
import com.example.cairnclone.game.board.*

class Spawning(boardState: BoardState) : GameState(boardState) {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is SpawnShaman -> ActionResult.NewState(
                this, listOf(
                    RemoveShaman(action.pos),
                    AddShaman(boardState.inactiveShamans.first { it.team == action.team }.toShaman(action.pos)),
                    MarkShamanAsMoved(boardState.inactiveShamans.first { it.team == action.team }.id),
                    FlipSpawnTile,
                    CompleteSpawning,
                )
            )
            is RemoveShaman -> removeShaman(action)
            is AddShaman -> addShaman(action)
            is MarkShamanAsMoved -> markShamanAsMoved(action)
            is FlipSpawnTile -> flipActionTile()
            is CompleteSpawning -> completeSpawning()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun markShamanAsMoved(action: MarkShamanAsMoved): ActionResult =
        ActionResult.NewState(
            Spawning(
                boardState = boardState.copy(
                    movedShamanIds = boardState.movedShamanIds + action.shamanId
                )
            )
        )

    private fun removeShaman(action: RemoveShaman): ActionResult =
        boardState.shamanAt(action.pos)?.let {
            ActionResult.NewState(
                Spawning(
                    boardState.copy(
                        inactiveShamans = boardState.inactiveShamans + it.toInactiveShaman(),
                        activeShamans = boardState.activeShamans - it
                    )
                )
            )
        } ?: ActionResult.NothingToDo


    private fun addShaman(action: AddShaman): ActionResult =
        ActionResult.NewState(
            Spawning(
                boardState.copy(
                    inactiveShamans = boardState.inactiveShamans - action.shaman.toInactiveShaman(),
                    activeShamans = boardState.activeShamans + action.shaman,
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
        tryActivatingMonolith(
            Pos(0, 0),
            { ActionResult.NewState(WaitForTransformation(boardState)) },
            boardState
        )

    private data class RemoveShaman(val pos: Pos) : Action
    private data class AddShaman(val shaman: Shaman) : Action
    private data class MarkShamanAsMoved(val shamanId: ShamanId) : Action
    private object FlipSpawnTile : Action
    private object CompleteSpawning : Action
}

private fun SpawnActionTile.flip(): SpawnActionTile = when (this) {
    SpawnActionTile.White -> SpawnActionTile.Black
    SpawnActionTile.Black -> SpawnActionTile.White
}
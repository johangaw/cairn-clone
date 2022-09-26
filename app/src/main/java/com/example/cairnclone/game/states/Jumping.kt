package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.JumpOverShaman
import com.example.cairnclone.game.board.*

class Jumping(override val boardState: BoardState) : GameState {
    override fun perform(action: Action): ActionResult {
        return when (action) {
            is JumpOverShaman -> ActionResult.NothingToDo(
                listOf(
                    Jump(action.jumper, action.newPos),
                    RemoveVillageJumper(action.newPos, action.jumper.team),
                    FlipJumpTile,
                    TryStartBuildMonolith(action.jumper.pos, action.newPos, action.jumper.team) {
                        TryActivateMonolith(action.jumper.team, action.newPos)
                        { CompleteJump }
                    })
            )
            is Jump -> jump(action)
            is RemoveVillageJumper -> removeVillageJumper(action)
            is FlipJumpTile -> flipJumpTile()
            is TryStartBuildMonolith -> tryStartBuildMonolith(action)
            is TryActivateMonolith -> tryActivateMonolith(action)
            is CompleteJump -> completeJump()
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun jump(action: Jump): ActionResult {
        return ActionResult.NewState(
            Jumping(
                boardState.copy(
                    activeShamans = boardState.activeShamans - action.jumper + action.jumper.copy(
                        pos = action.newPos
                    )
                )
            )
        )
    }

    private fun removeVillageJumper(action: RemoveVillageJumper): ActionResult =
        if (action.newPos in boardState.board.villageRowFor(action.jumpingTeam.other())) {
            val shaman = boardState.shamanAt(action.newPos)!!
            ActionResult.NewState(
                Jumping(
                    boardState.copy(
                        activeShamans = boardState.activeShamans - shaman,
                        inactiveShamans = boardState.inactiveShamans + shaman.toInactiveShaman()
                    )
                )
            )
        } else ActionResult.NothingToDo()

    private fun flipJumpTile(): ActionResult =
        ActionResult.NewState(
            Jumping(
                boardState.copy(
                    jumpActionTile = boardState.jumpActionTile.flip()
                )
            )
        )

    private fun tryStartBuildMonolith(action: TryStartBuildMonolith): ActionResult =
        if (action.to in boardState.board.villageRowFor(action.team.other())) {
            tryBuildMonolith(
                action.from,
                action.team,
                boardState
            ) { ActionResult.NewState(Jumping(it), listOf(action.nextAction())) }
        } else ActionResult.NothingToDo(listOf(action.nextAction()))


    private fun tryActivateMonolith(action: TryActivateMonolith): ActionResult =
        tryActivatingMonolith(
            action.pos,
            { ActionResult.NewState(Jumping(it), listOf(action.nextAction())) },
            boardState
        )

    private fun completeJump(): ActionResult =
        ActionResult.NewState(WaitForTransformation(boardState))

    private data class Jump(val jumper: Shaman, val newPos: Pos) : Action
    private data class RemoveVillageJumper(val newPos: Pos, val jumpingTeam: Team) : Action
    private object FlipJumpTile : Action
    private data class TryStartBuildMonolith(
        val from: Pos,
        val to: Pos,
        val team: Team,
        val nextAction: () -> Action
    ) : Action

    private data class TryActivateMonolith(
        val team: Team,
        val pos: Pos,
        val nextAction: () -> Action
    ) : Action

    private object CompleteJump : Action
}

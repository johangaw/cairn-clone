package com.example.cairnclone.game.states

import com.example.cairnclone.game.actions.Action
import com.example.cairnclone.game.actions.JumpOverShaman
import com.example.cairnclone.game.actions.MoveShaman
import com.example.cairnclone.game.actions.SpawnShaman
import com.example.cairnclone.game.board.*
import kotlin.math.abs

class WaitForAction(boardState: BoardState) : GameState(boardState) {

    override fun perform(action: Action): ActionResult {
        return when (action) {
            is SpawnShaman -> validateSpawnShaman(action)
            is MoveShaman -> validateMoveShaman(action)
            is JumpOverShaman -> validateJumpOverShaman(action)
            else -> ActionResult.InvalidAction(this, action)
        }
    }

    private fun validateSpawnShaman(action: SpawnShaman): ActionResult {
        return when {
            boardState.activeTeam != action.team -> ActionResult.InvalidAction("a shaman for team ${action.team} can not be spawn right now")
            @Suppress("ReplaceSizeZeroCheckWithIsEmpty")
            boardState.inactiveShamans.count { it.team == action.team } == 0 -> ActionResult.InvalidAction(
                "there are no shamans to spawn for team ${action.team}"
            )
            boardState.spawnActionTile.posFor(action.team) != action.pos -> ActionResult.InvalidAction(
                "the selected location (${action.pos}) is not an active spawn location for the current team ${action.team}"
            )
            boardState.shamanAt(action.pos)?.team == action.team -> ActionResult.InvalidAction("the selected spawn location (${action.pos}) already contains a shaman from the same team")
            else -> ActionResult.NewState(Spawning(boardState), listOf(action))
        }
    }

    private fun validateMoveShaman(action: MoveShaman): ActionResult {
        val shaman = action.shaman
        return when {
            !boardState.board.isOnBoardOrInVillage(action.newPos) -> ActionResult.InvalidAction("the selected pos ${action.newPos} is not on the board or in any village")
            !boardState.activeShamans.contains(shaman) -> ActionResult.InvalidAction("the selected shaman ${action.shaman} is not active")
            boardState.activeTeam != action.team -> ActionResult.InvalidAction("the team ${action.team} is not allowed to move a shaman right now")
            shaman.team != action.team -> ActionResult.InvalidAction("the selected shaman ${action.shaman} is not part of the ${action.team} team")
            action.newPos.x !in -1..boardState.board.width -> ActionResult.InvalidAction("newPos ${action.newPos} is outside of board and villages")
            action.newPos.y !in -1..boardState.board.height -> ActionResult.InvalidAction("newPos ${action.newPos} is outside of board and villages")
            boardState.shamanAt(action.newPos) != null -> ActionResult.InvalidAction("${action.newPos} already contains a shaman")
            !boardState.moveActionTile.possibleTargets(shaman.pos)
                .contains(action.newPos) -> ActionResult.InvalidAction("${boardState.moveActionTile} does not allow moving $shaman to ${action.newPos}")
            boardState.isInVillage(
                action.newPos,
                action.team
            ) -> ActionResult.InvalidAction("shaman $shaman can't move into it's own village")
            else -> ActionResult.NewState(Moving(boardState), listOf(action))
        }
    }

    private fun validateJumpOverShaman(action: JumpOverShaman): ActionResult {
        val (jumper, newPos) = action
        val springboardPos = between(jumper.pos, newPos)
        val springboard = springboardPos?.let { boardState.shamanAt(it) }
        return when {
            !boardState.activeShamans.contains(jumper) -> ActionResult.InvalidAction("the selected shaman $jumper is not active")
            springboardPos == null -> ActionResult.InvalidAction("unable to jump from ${jumper.pos} to $newPos")
            boardState.shamanAt(springboardPos) == null -> ActionResult.InvalidAction("there is no one to jump of at $springboardPos")
            jumper.team != boardState.activeTeam -> ActionResult.InvalidAction("the jumper must be of the active team ${boardState.activeTeam}")
            springboard == null -> ActionResult.InvalidAction("there is no one to jump over at $springboardPos")
            !boardState.jumpActionTile.isApplicable(jumper.team, springboard.team) -> ActionResult.InvalidAction("the jumper and the springboard are from incorrect teams")
            !boardState.board.isOnBoardOrInVillage(action.newPos) -> ActionResult.InvalidAction("the shaman would end up outside the board ${action.newPos}")
            boardState.shamanAt(action.newPos) != null -> ActionResult.InvalidAction("the landing location ${action.newPos} is occupied")
            else -> ActionResult.NewState(Jumping(boardState), listOf(action))
        }
    }

}

fun between(p1: Pos, p2: Pos): Pos? =
    Direction.values()
        .find { p1 + it + it == p2 }
        ?.let { p1 + it }

fun Board.isOnBoard(pos: Pos) =
    pos.x in (0 until width) && pos.y in (0 until height)

fun Board.isOnBoardOrInVillage(pos: Pos) =
    isOnBoard(pos)
            || villageRowFor(Team.Forest).contains(pos)
            || villageRowFor(Team.Sea).contains(pos)
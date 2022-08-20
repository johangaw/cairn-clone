package com.example.cairnclone.game.stages.monoliths

import com.example.cairnclone.game.Game
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.boardState
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.WaitForAction
import com.example.cairnclone.game.states.monoliths.ActivatingChaosOfTheGiants
import org.junit.Assert.*
import org.junit.Test

private fun monolithGame(init: BoardStateBuilder.() -> Unit): Triple<Game, Team, Pos> {
    val team = Team.Forest
    val pos = Pos(2, 2)
    val game = Game(
        ActivatingChaosOfTheGiants(
            buildBoard {
                emptyBoard()
                addInactiveShamans()
                positionMonolith(MonolithType.ChaosOfTheGiants, pos)
                positionForestShaman(pos)
                this.init()
            },
            team
        ) { ActionResult.NewState(WaitForAction(it)) }
    )
    return Triple(game, team, pos)
}

class TestActivatingChaosOfTheGiants {

    @Test
    fun `when trying to banish an inactive shaman, it returns an error`() {
        val (game) = monolithGame {}
        val shaman = game.boardState.inactiveShamans.first()
        val result = game.perform(ActivatingChaosOfTheGiants.Activate(shaman.toShaman(Pos(0,0))))
        assertFalse(result)
    }

    @Test
    fun `when trying to banish a friendly shaman, it returns an error`() {
        val pos = Pos(0, 0)
        val (game) = monolithGame {
            positionForestShaman(pos)
        }
        val result =
            game.perform(ActivatingChaosOfTheGiants.Activate(game.boardState.shamanAt(pos)!!))
        assertFalse(result)
    }

    @Test
    fun `when trying to banish an enemy shaman not in the first row, it returns an error`() {
        val pos = Pos(0, 1)
        val (game) = monolithGame {
            positionSeaShaman(pos)
        }
        val result =
            game.perform(ActivatingChaosOfTheGiants.Activate(game.boardState.shamanAt(pos)!!))
        assertFalse(result)
    }

    @Test
    fun `when trying to banish an enemy shaman in the first row, it removes the shaman and returns success`() {
        val pos = Pos(0, 0)
        val (game) = monolithGame {
            positionSeaShaman(pos)
        }
        val shaman = game.boardState.shamanAt(pos)!!
        val result = game.perform(ActivatingChaosOfTheGiants.Activate(shaman))
        assertTrue(result)
        assertNull(game.boardState.shamanAt(pos))
        assertTrue(game.boardState.inactiveShamans.contains(shaman.toInactiveShaman()))
        assertTrue("invalid final state ${game.gameState}", game.gameState is WaitForAction)
    }

    @Test
    fun `when trying to skip while there is a shaman to banish, it returns an error`() {
        val pos = Pos(0, 0)
        val (game) = monolithGame {
            positionSeaShaman(pos)
        }
        val result = game.perform(ActivatingChaosOfTheGiants.Skipp)
        assertFalse(result)
    }

    @Test
    fun `when trying to skip while there is no shaman to banish, it returns success and moves on to the next state`() {
        val (game) = monolithGame {}
        val result = game.perform(ActivatingChaosOfTheGiants.Skipp)
        assertTrue(result)
        assertTrue("invalid final state ${game.gameState}", game.gameState is WaitForAction)
    }
}

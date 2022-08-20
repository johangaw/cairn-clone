package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Game
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.BoardStateBuilder
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Team
import com.example.cairnclone.game.board.buildBoard
import com.example.cairnclone.game.boardState
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.WaitForAction
import org.junit.Assert.*
import org.junit.Test

private fun monolithGame(init: BoardStateBuilder.() -> Unit): Triple<Game, Team, Pos> {
    val team = Team.Forest
    val pos = Pos(2, 2)
    val game = Game(
        ActivateCairnOfDawn(
            buildBoard {
                emptyBoard()
                addInactiveShamans()
                positionMonolith(MonolithType.CairnOfDawn, pos)
                positionForestShaman(pos)
                this.init()
            },
            team
        ) { ActionResult.NewState(WaitForAction(it)) }
    )
    return Triple(game, team, pos)
}

class ActivateCairnOfDawnTest {

    @Test
    fun `when there is no inactive shaman it is possible to skip`() {
        val ( game ) = monolithGame {
            repeat(4) {
                positionForestShaman(Pos(it,1))
            }
        }

        val result = game.perform(ActivateCairnOfDawn.Skipp)

        assertTrue(result)
        assertTrue(game.gameState is WaitForAction)
    }

    @Test
    fun `when there is no room in the first row it is possible to skip`() {
        val ( game ) = monolithGame {
            repeat(5) {
                positionSeaShaman(Pos(it,0))
            }
        }

        val result = game.perform(ActivateCairnOfDawn.Skipp)

        assertTrue(result)
        assertTrue(game.gameState is WaitForAction)
    }

    @Test
    fun `when it is possible to spawn a shaman skip return false`() {
        val ( game ) = monolithGame {}
        val result = game.perform(ActivateCairnOfDawn.Skipp)
        assertFalse(result)
    }

    @Test
    fun `when there are no inactive shaman to spawn Activate returns false`(){
        val ( game ) = monolithGame {
            repeat(4) {
                positionForestShaman(Pos(it, 1))
            }
        }
        val result = game.perform(ActivateCairnOfDawn.Activate(Pos(2,0)))
        assertFalse(result)
    }

    @Test
    fun `when the selected pos is occupied Activate returns false`(){
        val pos = Pos(2, 0)
        val ( game ) = monolithGame {
            positionForestShaman(pos)
        }
        val result = game.perform(ActivateCairnOfDawn.Activate(pos))
        assertFalse(result)
    }

    @Test
    fun `when selected pos is not in the first row of the team Activate returns false`(){
        val ( game ) = monolithGame {}
        val result = game.perform(ActivateCairnOfDawn.Activate(Pos(2,4)))
        assertFalse(result)
    }

    @Test
    fun `when the pos is in the first row and free and there are inactive shamans Activate returns true, spawns a shaman and progress to the next state`(){
        val ( game, team ) = monolithGame {}
        val result = game.perform(ActivateCairnOfDawn.Activate(Pos(2,0)))
        assertTrue(result)
        assertEquals(game.boardState.shamanAt(Pos(2,0))?.team, team)
        assertTrue(game.gameState is WaitForAction)
    }
}
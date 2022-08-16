package com.example.cairnclone.game

import com.example.cairnclone.game.actions.EndTurn
import com.example.cairnclone.game.actions.MoveShaman
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.states.WaitForAction
import org.junit.Assert.*
import org.junit.Test

class MovementTest {

    @Test
    fun `when a shaman performs a valid orthogonal move it updates its position and return true`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(1, 1))
            moveAction(MoveActionTile.Orthogonally)
        }
        val shaman = game.gameState.boardState.activeShamans.first()

        val result = game.perform(MoveShaman(shaman, Team.Forest, newPos = Pos(1, 2)))

        assertTrue(result)
        assertEquals(game.gameState.boardState.activeShamans.first().pos, Pos(1, 2))
    }

    @Test
    fun `when a shaman performs a valid diagonal move it updates its position and return true`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(3, 3))
            moveAction(MoveActionTile.Diagonally)
        }
        val shaman = game.gameState.boardState.activeShamans.first()

        val result = game.perform(MoveShaman(shaman, Team.Forest, newPos = Pos(2, 2)))

        assertTrue(result)
        assertEquals(game.gameState.boardState.activeShamans.first().pos, Pos(2, 2))
    }

    @Test
    fun `when an EndTurn is send after a movement the turn is passed to the other team`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(1, 0))
            moveAction(MoveActionTile.Orthogonally)
        }
        val shaman = game.gameState.boardState.activeShamans.first()

        val result = game.perform(
            MoveShaman(shaman, Team.Forest, newPos = Pos(1, 1)),
            EndTurn
        )

        assertTrue(result)
        assertTrue(game.gameState is WaitForAction)
        assertEquals(game.gameState.boardState.activeTeam, Team.Sea)
    }
}

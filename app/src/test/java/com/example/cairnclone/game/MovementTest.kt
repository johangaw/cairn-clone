package com.example.cairnclone.game

import com.example.cairnclone.game.actions.EndTurn
import com.example.cairnclone.game.actions.MoveShaman
import com.example.cairnclone.game.actions.SelectMonolith
import com.example.cairnclone.game.board.MonolithType
import com.example.cairnclone.game.board.MoveActionTile
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Team
import com.example.cairnclone.game.states.WaitForAction
import com.example.cairnclone.game.states.WaitForTransformation
import com.example.cairnclone.game.states.monoliths.ActivatingChaosOfTheGiants
import org.junit.Assert.*
import org.junit.Test

class MovementTest {
    @Test
    fun `when a shaman tries to move off the field it return false`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(4, 2))
            moveAction = MoveActionTile.Orthogonally
        }
        val shaman = game.boardState.shamanAt(Pos(4,2))!!
        val result = game.perform(MoveShaman(shaman, newPos = Pos(5, 2)))

        assertFalse(result)
    }

    @Test
    fun `when a shaman performs a valid orthogonal move it updates its position and return true`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(1, 1))
            moveAction = MoveActionTile.Orthogonally
        }
        val shaman = game.boardState.shamanAt(Pos(1,1))!!
        val result = game.perform(MoveShaman(shaman, newPos = Pos(1, 2)))

        assertTrue(result)
        assertEquals(game.gameState.boardState.activeShamans.first().pos, Pos(1, 2))
    }

    @Test
    fun `when a shaman performs a valid diagonal move it updates its position and return true`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(3, 3))
            moveAction = MoveActionTile.Diagonally
        }
        val shaman = game.boardState.shamanAt(Pos(3,3))!!
        val result = game.perform(MoveShaman(shaman, newPos = Pos(2, 2)))

        assertTrue(result)
        assertEquals(game.gameState.boardState.activeShamans.first().pos, Pos(2, 2))
    }

    @Test
    fun `when an EndTurn is send after a movement the turn is passed to the other team`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(1, 0))
            moveAction = MoveActionTile.Orthogonally
        }
        val shaman = game.boardState.shamanAt(Pos(1,0))!!

        val result = game.perform(
            MoveShaman(shaman, newPos = Pos(1, 1)),
            EndTurn
        )

        assertTrue(result)
        assertTrue(game.gameState is WaitForAction)
        assertEquals(game.gameState.boardState.activeTeam, Team.Sea)
    }

    @Test
    fun `when moving a shaman onto a monolith it activates that monolith`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(0, 0))
            positionMonolith(MonolithType.ChaosOfTheGiants, Pos(0, 1))
            activeTeam = Team.Forest
            moveAction = MoveActionTile.Orthogonally

            positionSeaShaman(Pos(2, 0))  // shaman to banish
        }
        val shaman = game.boardState.shamanAt(Pos(0, 0))!!
        val result = game.perform(MoveShaman(shaman, Pos(0, 1)))

        assertTrue(result)
        assertTrue(
            "not correct class ${game.gameState.javaClass.simpleName}",
            game.gameState is ActivatingChaosOfTheGiants
        )
    }

    @Test
    fun `when a shaman tries to exit through their own village it return false`() {
        val pos = Pos(0, 0)
        val game = game {
            emptyBoard()
            positionForestShaman(pos)
            activeTeam = Team.Forest
            moveAction = MoveActionTile.Orthogonally
        }
        val shaman = game.boardState.shamanAt(pos)!!

        val result = game.perform(MoveShaman(shaman, Pos(0, -1)))
        assertFalse(result)
    }

    @Test
    fun `when the moved shaman exits through the opponents village it removes the shaman, builds a monolith and continue to the transformation stage`() {
        val pos = Pos(0, 4)
        val game = game {
            emptyBoard()
            positionForestShaman(pos)
            activeTeam = Team.Forest
            moveAction = MoveActionTile.Diagonally
        }
        val newMonolith = game.boardState.monolithsStack.first()
        val shaman = game.boardState.shamanAt(pos)!!

        val res1 = game.perform(MoveShaman(shaman, Pos(-1, 5)))
        val res2 = game.perform(SelectMonolith(newMonolith))

        assertTrue(res1)
        assertTrue(res2)
        assertNull(game.boardState.shamanAt(pos))
        assertEquals(game.boardState.monolithAt(pos)?.type, newMonolith)
        assertTrue(
            "not correct state ${game.gameState.javaClass.simpleName}",
            game.gameState is WaitForTransformation
        )
    }
}

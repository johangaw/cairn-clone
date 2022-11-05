package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Game
import com.example.cairnclone.game.board.BoardStateBuilder
import com.example.cairnclone.game.board.MonolithType
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.buildBoard
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.WaitForAction
import org.junit.Assert.*
import org.junit.Test

private fun monolithGame(monolithPos: Pos = Pos(2,2), init: BoardStateBuilder.() -> Unit): Pair<Game, ActivatingMenhirOfTheDancers> {
    val boardState = buildBoard {
        emptyBoard()
        addInactiveShamans()
        positionMonolith(MonolithType.MenhirOfTheDancers, monolithPos)
        positionForestShaman(monolithPos)
        this.init()
    }
    val state = ActivatingMenhirOfTheDancers(
        boardState,
        boardState.monolithAt(monolithPos)!!,
        boardState.shamanAt(monolithPos)!!
    ) { ActionResult.NewState(WaitForAction(it)) }
    val game = Game(state)
    return Pair(game, state)
}

class ActivatingMenhirOfTheDancersTest {
    @Test
    fun `when there is no free position around the monolith, canActivate returns false`() {
        val (_, state) = monolithGame(Pos(0,2)) {
            positionForestShaman(Pos(0, 1))
            positionForestShaman(Pos(0, 3))

            positionSeaShaman(Pos(1, 1))
            positionSeaShaman(Pos(1, 2))
            positionSeaShaman(Pos(1, 3))
        }
        assertFalse(state.canActivate())
    }

    @Test
    fun `when there is a free position adjacent to the monolith, canActivate returns true`() {
        val (_, state) = monolithGame {}
        assertTrue(state.canActivate())
    }

    @Test
    fun `when a position not adjacent to the monolith is selected, MoveShaman returns false`() {
        val (game) = monolithGame {}
        val result = game.perform(ActivatingMenhirOfTheDancers.MoveShaman(Pos(4,1)))
        assertFalse(result)
    }

    @Test
    fun `when a occupied position is selected, MoveShaman returns false`() {
        val (game) = monolithGame {
            positionSeaShaman(Pos(3,2))
        }
        val result = game.perform(ActivatingMenhirOfTheDancers.MoveShaman(Pos(3,2)))
        assertFalse(result)
    }

    @Test
    fun `when a position outside the board is selected, MoveShaman returns false`() {
        val (game) = monolithGame(Pos(0,0)) {}
        val result = game.perform(ActivatingMenhirOfTheDancers.MoveShaman(Pos(-1,0)))
        assertFalse(result)
    }

    @Test
    fun `when a position in their own village is selected, MoveShaman returns false`() {
        val (game) = monolithGame(Pos(0,0)) {}
        val result = game.perform(ActivatingMenhirOfTheDancers.MoveShaman(Pos(0,-1)))
        assertFalse(result)
    }

    @Test
    fun `when free position adjacent to the monolith is selected, MoveShaman returns true`() {
        val (game) = monolithGame {}
        val result = game.perform(ActivatingMenhirOfTheDancers.MoveShaman(Pos(3,3)))
        assertTrue(result)
        assertTrue(game.gameState is WaitForAction)
    }

    @Test
    fun `when the shaman is moved into the other teams village, MoveShaman returns true and the shaman is removed from the game`() {
        val (game) = monolithGame(Pos(2,4)) {}
        val result = game.perform(ActivatingMenhirOfTheDancers.MoveShaman(Pos(2,5)))
        assertTrue(result)
        assertTrue(game.gameState is WaitForAction)
        assertEquals(game.gameState.boardState.activeShamans.size, 0)
    }

    @Test
    fun `when the shaman is moved to another monolith, that monolith is activated`() {
        val (game) = monolithGame(Pos(2,2)) {
            positionMonolith(MonolithType.CairnOfDawn, Pos(2,3))
        }
        val result = game.perform(ActivatingMenhirOfTheDancers.MoveShaman(Pos(2,3)))
        assertTrue(result)
        assertTrue(game.gameState is ActivatingCairnOfDawn)
    }
}
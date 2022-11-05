package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Game
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.boardState
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.WaitForAction
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private fun monolithGame(init: BoardStateBuilder.() -> Unit): Pair<Game, ActivatingDeerRock> {
    val pos = Pos(2, 2)
    val boardState = buildBoard {
        emptyBoard()
        addInactiveShamans()
        positionMonolith(MonolithType.DeerRock, pos)
        positionForestShaman(pos)
        this.init()
    }
    val state = ActivatingDeerRock(
        boardState,
        boardState.monolithAt(pos)!!,
        boardState.shamanAt(pos)!!
    ) { ActionResult.NewState(WaitForAction(it)) }
    val game = Game(state)
    return Pair(game, state)
}

class ActivatingDeerRockTest {

    @Test
    fun `when there are no shaman adjacent to the monolith canActivate returns false`() {
        val (_,  state) = monolithGame {}
        assertFalse(state.canActivate())
    }

    @Test
    fun `when there are a friendly shaman adjacent to the monolith canActivate returns true`() {
        val (_,  state) = monolithGame {
            positionForestShaman(Pos(2,3))
        }
        assertTrue(state.canActivate())
    }

    @Test
    fun `when there are a friendly shaman adjacent to the monolith which can't move canActivate returns false`() {
        val monolithPos = Pos(0,0)
        val boardState = buildBoard {
            emptyBoard()
            addInactiveShamans()
            positionMonolith(MonolithType.DeerRock, monolithPos)
            positionForestShaman(monolithPos)
            positionForestShaman(Pos(1,0))
            positionForestShaman(Pos(0,1))
            positionForestShaman(Pos(1,1))

            positionSeaShaman(Pos(2,0))
            positionSeaShaman(Pos(2,1))
            positionSeaShaman(Pos(2,2))
            positionSeaShaman(Pos(1,2))
            positionSeaShaman(Pos(0,2))
        }
        val state = ActivatingDeerRock(
            boardState,
            boardState.monolithAt(monolithPos)!!,
            boardState.shamanAt(monolithPos)!!
        ) { ActionResult.NewState(WaitForAction(it)) }
        assertFalse(state.canActivate())
    }

    @Test
    fun `when the shaman is not an active shaman, MoveShaman returns false`() {
        val (game) = monolithGame {}
        val result = game.perform(ActivatingDeerRock.MoveShaman(Shaman(team = Team.Forest, pos = Pos(2,3)), Pos(3,3)))
        assertFalse(result)
    }

    @Test
    fun `when the shaman is not adjacent to the monolith, MoveShaman returns false`() {
        val pos = Pos(4, 4)
        val (game) = monolithGame {
            positionForestShaman(pos)
        }
        val result = game.perform(ActivatingDeerRock.MoveShaman(game.boardState.shamanAt(pos)!!, Pos(4,3)))
        assertFalse(result)
    }

    @Test
    fun `when the new position is not adjacent to the selected shaman, MoveShaman returns false`() {
        val pos = Pos(2, 3)
        val (game) = monolithGame {
            positionForestShaman(pos)
        }
        val result = game.perform(ActivatingDeerRock.MoveShaman(game.boardState.shamanAt(pos)!!, Pos(1,1)))
        assertFalse(result)
    }

    @Test
    fun `when the new position is occupied by another shaman, MoveShaman returns false`() {
        val shamanPos = Pos(1, 1)
        val newPos = Pos(1,2)
        val (game) = monolithGame {
            positionForestShaman(shamanPos)
            positionSeaShaman(newPos)
        }
        val result = game.perform(ActivatingDeerRock.MoveShaman(game.boardState.shamanAt(shamanPos)!!, newPos))
        assertFalse(result)
    }

    @Test
    fun `when shaman is adjacent to the monolith and the new position is free and adjacent to the shaman, MoveShaman returns true`() {
        val pos = Pos(2, 1)
        val (game) = monolithGame {
            positionForestShaman(pos)
        }
        val result = game.perform(ActivatingDeerRock.MoveShaman(game.boardState.shamanAt(pos)!!, Pos(3, 1)))
        assertTrue(result)
        assertTrue(game.gameState is WaitForAction)
    }

    @Test
    fun `when a shaman is moved onto a monolith that monolith is activated`() {
        val pos = Pos(2, 1)
        val (game) = monolithGame {
            positionForestShaman(pos)
            positionMonolith(MonolithType.CairnOfDawn, Pos(1,1))
        }
        val result = game.perform(ActivatingDeerRock.MoveShaman(game.boardState.shamanAt(pos)!!, Pos(1, 1)))
        assertTrue(result)
        assertTrue(game.gameState is ActivatingCairnOfDawn)
    }
}
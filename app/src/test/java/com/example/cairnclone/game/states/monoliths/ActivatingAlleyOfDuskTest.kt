package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Game
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.boardState
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.WaitForAction
import org.junit.Assert.*
import org.junit.Test

private fun monolithGame(init: BoardStateBuilder.() -> Unit): Pair<Game, ActivatingAlleyOfDusk> {
    val pos = Pos(2, 2)
    val boardState = buildBoard {
        emptyBoard()
        addInactiveShamans()
        positionMonolith(MonolithType.AlleyOfDusk, pos)
        positionForestShaman(pos)
        this.init()
    }
    val state = ActivatingAlleyOfDusk(
        boardState,
        boardState.monolithAt(pos)!!,
        boardState.shamanAt(pos)!!
    ) { ActionResult.NewState(WaitForAction(it)) }
    val game = Game(state)
    return Pair(game, state)
}

class ActivatingAlleyOfDuskTest {
    @Test
    fun `when there is an enemy shaman adjacent to the monolith canActivate return true`() {
        val (_, state) = monolithGame {
            positionSeaShaman(Pos(3, 3))
        }
        assertTrue(state.canActivate())
    }

    @Test
    fun `when there are no enemy shaman adjacent to the monolith canActivate return false`() {
        val (_, state) = monolithGame {
            positionSeaShaman(Pos(0, 1))
        }
        assertFalse(state.canActivate())
    }

    @Test
    fun `when an adjacent enemy shaman is selected it removes that shaman, transition to the next state and returns true`() {
        val (game) = monolithGame {
            positionSeaShaman(Pos(3, 3))
        }
        val result = game.perform(
            ActivatingAlleyOfDusk.BanishShaman(
                game.boardState.shamanAt(Pos(3, 3))!!
            )
        )
        assertTrue(result)
        assertNull(game.boardState.shamanAt(Pos(3,3)))
        assertTrue(game.gameState is WaitForAction)
    }

    @Test
    fun `when the selected shaman is not active it returns false`() {
        val (game) = monolithGame {}
        val result = game.perform(
            ActivatingAlleyOfDusk.BanishShaman(Shaman(team = Team.Sea, pos = Pos(3,3)))
        )
        assertFalse(result)
    }

    @Test
    fun `when the selected shaman is not an enemy shaman it returns false`() {
        val (game) = monolithGame {
            positionForestShaman(Pos(3, 3))
        }
        val result = game.perform(
            ActivatingAlleyOfDusk.BanishShaman(
                game.boardState.shamanAt(Pos(3, 3))!!
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when the selected shaman is not adjacent it returns false`() {
        val (game) = monolithGame {
            positionSeaShaman(Pos(3, 4))
        }
        val result = game.perform(
            ActivatingAlleyOfDusk.BanishShaman(
                game.boardState.shamanAt(Pos(3, 4))!!
            )
        )
        assertFalse(result)
    }
}
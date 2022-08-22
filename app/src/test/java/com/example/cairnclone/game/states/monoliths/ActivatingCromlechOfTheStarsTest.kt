package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Game
import com.example.cairnclone.game.Monolith
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

private fun monolithGame(init: BoardStateBuilder.() -> Unit): Triple<Game, Team, ActivatingCromlechOfTheStars> {
    val team = Team.Forest
    val pos = Pos(2, 2)
    val boardState = buildBoard {
        emptyBoard()
        addInactiveShamans()
        positionMonolith(MonolithType.CromlechOfTheStars, pos)
        positionForestShaman(pos)
        this.init()
    }
    val state = ActivatingCromlechOfTheStars(
        boardState,
        boardState.monolithAt(pos)!!,
        boardState.shamanAt(pos)!!
    ) { ActionResult.NewState(WaitForAction(it)) }
    val game = Game(state)
    return Triple(game, team, state)
}

class ActivatingCromlechOfTheStarsTest {

    @Test
    fun `when there are no other monoliths canActivate returns false`() {
        val (_, _, state) = monolithGame { }
        assertFalse(state.canActivate())
    }

    @Test
    fun `when all monoliths are occupied canActivate returns false`() {
        val (_, _, state) = monolithGame {
            positionMonolith(MonolithType.CairnOfDawn, Pos(4, 4))
            positionSeaShaman(Pos(4, 4))
        }
        assertFalse(state.canActivate())
    }

    @Test
    fun `when there is a free megalith canActivate returns true`() {
        val (_, _, state) = monolithGame {
            positionMonolith(MonolithType.CairnOfDawn, Pos(4, 4))
        }
        assertTrue(state.canActivate())
    }

    @Test
    fun `when the selected monolith is not active MoveToMonolith returns false`() {
        val (game) = monolithGame {
            positionMonolith(MonolithType.CairnOfDawn, Pos(4,4))
        }
        val result = game.perform(
            ActivatingCromlechOfTheStars.MoveToMonolith(
                Monolith(Pos(1,1), MonolithType.CairnOfDawn)
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when the selected monolith is occupied MoveToMonolith returns false`() {
        val (game) = monolithGame {
            positionMonolith(MonolithType.CairnOfDawn, Pos(4,4))
            positionForestShaman(Pos(4,4))
        }
        val result = game.perform(
            ActivatingCromlechOfTheStars.MoveToMonolith(
                game.boardState.monolithAt(Pos(4,4))!!
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when the shaman can be moved MoveToMonolith moves the shaman, activates the other monolith and returns true`() {
        val (game) = monolithGame {
            positionMonolith(MonolithType.CairnOfDawn, Pos(4,4))
        }
        val result = game.perform(
            ActivatingCromlechOfTheStars.MoveToMonolith(
                game.boardState.monolithAt(Pos(4,4))!!
            )
        )
        assertTrue(result)
        assertTrue(game.gameState is ActivatingCairnOfDawn)
        assertNull(game.boardState.shamanAt(Pos(2,2)))
        assertNotNull(game.boardState.shamanAt(Pos(4,4)))
    }

    @Test
    fun `when the next monolith can't activate MoveToMonolith moves the shaman and returns true`() {
        val (game) = monolithGame {
            positionMonolith(MonolithType.ChaosOfTheGiants, Pos(4,4))
        }
        val result = game.perform(
            ActivatingCromlechOfTheStars.MoveToMonolith(
                game.boardState.monolithAt(Pos(4,4))!!
            )
        )
        assertTrue(result)
        assertTrue(game.gameState is WaitForAction)
        assertNull(game.boardState.shamanAt(Pos(2,2)))
        assertNotNull(game.boardState.shamanAt(Pos(4,4)))
    }
}
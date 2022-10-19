package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Game
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.BoardStateBuilder
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.buildBoard
import com.example.cairnclone.game.states.ActionResult
import com.example.cairnclone.game.states.WaitForAction
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private fun monolithGame(monolithPos: Pos, init: BoardStateBuilder.() -> Unit): Pair<Game, ActivatingSanctuaryOfTheAges> {
    val boardState = buildBoard {
        emptyBoard()
        addInactiveShamans()
        positionMonolith(MonolithType.SanctuaryOfTheAges, monolithPos)
        positionForestShaman(monolithPos)
        this.init()
    }
    val state = ActivatingSanctuaryOfTheAges(
        boardState,
        boardState.monolithAt(monolithPos)!!,
        boardState.shamanAt(monolithPos)!!
    ) { ActionResult.NewState(WaitForAction(it)) }
    val game = Game(state)
    return Pair(game, state)
}

class ActivatingSanctuaryOfTheAgesTest {
    @Test
    fun `when the monolith is surrounded by other monoliths, canActivate returns false`() {
        val (_, state) = monolithGame(Pos(0,0)) {
            positionMonolith(MonolithType.DeerRock, Pos(1,0))
            positionMonolith(MonolithType.MenhirOfTheDancers, Pos(1,1))
            positionMonolith(MonolithType.ChaosOfTheGiants, Pos(0,1))
        }
        assertFalse(state.canActivate())
    }

    @Test
    fun `when the monolith can be moved, canActivate returns false`() {
        val (_, state) = monolithGame(Pos(2,2)) {}
        assertTrue(state.canActivate())
    }

    @Test
    fun `when the pos does not contain a monolith, MoveMonolith returns true and moves the monolith`() {
        val (game) = monolithGame(Pos(2,2)) {}
        val result = game.perform(ActivatingSanctuaryOfTheAges.MoveMonolith(Pos(3,3)))
        assertTrue(result)
        assertTrue(game.gameState is WaitForAction)
    }

    @Test
    fun `when the position contains another monolith, MovMonolith returns false`(){
        val (game) = monolithGame(Pos(2,2)) {
            positionMonolith(MonolithType.DeerRock, Pos(2,3))
        }
        val result = game.perform(ActivatingSanctuaryOfTheAges.MoveMonolith(Pos(2,3)))
        assertFalse(result)
    }

    @Test
    fun `when the position is outside of the board, MoveMonolith returns false`(){
        val (game) = monolithGame(Pos(4,4)) {}
        val result = game.perform(ActivatingSanctuaryOfTheAges.MoveMonolith(Pos(4,5)))
        assertFalse(result)
    }
}

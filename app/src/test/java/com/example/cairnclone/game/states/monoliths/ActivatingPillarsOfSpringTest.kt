package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.EndTurn
import com.example.cairnclone.game.actions.MoveShaman
import com.example.cairnclone.game.board.MoveActionTile
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Team
import com.example.cairnclone.game.boardState
import com.example.cairnclone.game.game
import com.example.cairnclone.game.states.WaitForAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActivatingPillarsOfSpringTest {

    @Test
    fun `when the current turn ends it is the same team that should go again`() {
        val game = game {
            emptyBoard()
            addInactiveShamans()
            moveAction = MoveActionTile.Orthogonally
            activeTeam = Team.Forest
            positionForestShaman(Pos(2, 1))
            positionMonolith(MonolithType.PillarsOfSpring, Pos(2, 2))
        }

        assertTrue(
            game.perform(MoveShaman(game.boardState.shamanAt(Pos(2, 1))!!, Team.Forest, Pos(2, 2)))
        )
        assertTrue(
            game.perform(ActivatingPillarsOfSpring.MakeNextTurnMyTurn)
        )
        assertTrue(game.perform(EndTurn))
        assertTrue(game.gameState is WaitForAction)
        assertEquals(game.boardState.activeTeam, Team.Forest)


    }
}
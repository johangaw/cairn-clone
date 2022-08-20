package com.example.cairnclone.game

import com.example.cairnclone.game.actions.JumpOverShaman
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.states.ActivatingChaosOfTheGiants
import com.example.cairnclone.game.states.WaitForTransformation
import org.junit.Assert.*
import org.junit.Test

private fun jumpGame(init: BoardStateBuilder.() -> Unit) = game {
    emptyBoard()
    activeTeam = Team.Forest
    jumpAction = JumpActionTile.OverTeamMate
    this.init()
}

class JumpingTest {

    @Test
    fun `when the jumper is not an active shaman it returns false`() {
        val game = jumpGame {
            positionForestShaman(Pos(2, 2))
            addInactiveShamans()
        }
        val result = game.perform(
            JumpOverShaman(
                game.boardState.inactiveShamans.first { it.team == Team.Forest }
                    .toShaman(Pos(1, 1)),
                game.boardState.activeShamans.first()
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when the springboard is not an active shaman it returns false`() {
        val game = jumpGame {
            positionForestShaman(Pos(2, 2))
            addInactiveShamans()
        }
        val result = game.perform(
            JumpOverShaman(
                game.boardState.activeShamans.first(),
                game.boardState.inactiveShamans.first { it.team == Team.Forest }
                    .toShaman(Pos(1, 1)),
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when the two shamans are not adjacent to one another it returns false`() {
        val game = jumpGame {
            positionForestShaman(Pos(2, 2))
            positionForestShaman(Pos(0, 2))
        }
        val result = game.perform(
            JumpOverShaman(
                game.boardState.activeShamans[0],
                game.boardState.activeShamans[1],
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when the jumper would end up outside the board it returns false`() {
        val game = jumpGame {
            positionForestShaman(Pos(3, 4))
            positionForestShaman(Pos(4, 4))
        }
        val result = game.perform(
            JumpOverShaman(
                game.boardState.shamanAt(Pos(3,4))!!,
                game.boardState.shamanAt(Pos(4,4))!!,
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when the jumper would end up on top of another shaman it returns false`() {
        val game = jumpGame {
            positionForestShaman(Pos(0, 0))
            positionForestShaman(Pos(1, 1))
            positionForestShaman(Pos(2, 2))
        }
        val result = game.perform(
            JumpOverShaman(
                game.boardState.shamanAt(Pos(0,0))!!,
                game.boardState.shamanAt(Pos(1,1))!!,
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when the jumper has the wrong team it returns false`() {
        val game = jumpGame {
            activeTeam = Team.Sea
            positionForestShaman(Pos(0, 0))
            positionForestShaman(Pos(1, 1))
        }
        val result = game.perform(
            JumpOverShaman(
                game.boardState.shamanAt(Pos(0,0))!!,
                game.boardState.shamanAt(Pos(1,1))!!,
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when the springboard should be an team mate but is not it returns false`() {
        val game = jumpGame {
            jumpAction = JumpActionTile.OverTeamMate
            positionForestShaman(Pos(0, 0))
            positionSeaShaman(Pos(1, 1))
        }
        val result = game.perform(
            JumpOverShaman(
                game.boardState.shamanAt(Pos(0,0))!!,
                game.boardState.shamanAt(Pos(1,1))!!,
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when the springboard should be an opponent but is not it returns false`() {
        val game = jumpGame {
            jumpAction = JumpActionTile.OverOpponent
            positionForestShaman(Pos(0, 0))
            positionForestShaman(Pos(1, 1))
        }
        val result = game.perform(
            JumpOverShaman(
                game.boardState.shamanAt(Pos(0,0))!!,
                game.boardState.shamanAt(Pos(1,1))!!,
            )
        )
        assertFalse(result)
    }

    @Test
    fun `when a valid jump is performed it moves the jumper to the correct location, updates the jump tile, transition to the transformation stage and returns true`() {
        val game = jumpGame {
            positionForestShaman(Pos(1, 1))
            positionForestShaman(Pos(2, 2))
            jumpAction = JumpActionTile.OverTeamMate
        }
        val jumper = game.boardState.shamanAt(Pos(1, 1))!!
        val springboard = game.boardState.shamanAt(Pos(2, 2))!!

        val result = game.perform(JumpOverShaman(jumper, springboard))

        assertTrue(result)
        assertEquals(game.boardState.shamanAt(Pos(2,2)), springboard)
        assertNull(game.boardState.shamanAt(jumper.pos))
        assertEquals(jumper.id, game.boardState.shamanAt(Pos(3,3))?.id)
        assertEquals(game.boardState.jumpActionTile, JumpActionTile.OverOpponent)
        assertTrue(
            "not correct class ${game.gameState.javaClass.simpleName}",
            game.gameState is WaitForTransformation
        )
    }

    @Test
    fun `when the jumper ends up on a monolith it activates the monolith and returns true`() {
        val game = jumpGame {
            positionForestShaman(Pos(1, 1))
            positionForestShaman(Pos(2, 2))
            positionMonolith(MonolithType.ChaosOfTheGiants, Pos(3,3))
        }
        val jumper = game.boardState.shamanAt(Pos(1, 1))!!
        val springboard = game.boardState.shamanAt(Pos(2, 2))!!

        val result = game.perform(JumpOverShaman(jumper, springboard))

        assertTrue(result)
        assertTrue(
            "not correct class ${game.gameState.javaClass.simpleName}",
            game.gameState is ActivatingChaosOfTheGiants
        )
    }
}
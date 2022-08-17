package com.example.cairnclone.game

import com.example.cairnclone.game.actions.MoveShaman
import com.example.cairnclone.game.actions.SelectMonolith
import com.example.cairnclone.game.actions.SpawnShaman
import com.example.cairnclone.game.actions.TransformShaman
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.states.WaitForAction
import com.example.cairnclone.game.states.WaitForTransformation
import org.junit.Assert
import org.junit.Test

class TransformationTest {

    @Test
    fun `when move allows for outnumbered transformation it removes the enemy shaman and spawn a monolith`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(0, 0))
            positionForestShaman(Pos(2, 0))
            positionSeaShaman(Pos(3, 0))
            moveAction(MoveActionTile.Orthogonally)
            transformation(TransformationTile.Outnumbered)
        }
        val s1 = game.gameState.boardState.shamanAt(Pos(0, 0))!!

        game.apply {
            perform(MoveShaman(s1, Team.Forest, Pos(1, 0)))
            perform(
                TransformShaman(
                    game.gameState.boardState.shamanAt(Pos(1, 0))!!,
                    game.gameState.boardState.shamanAt(Pos(2, 0))!!,
                    game.gameState.boardState.shamanAt(Pos(3, 0))!!,
                )
            )
            perform(SelectMonolith(game.gameState.boardState.monolithsStack.first()))
        }

        Assert.assertTrue(game.gameState is WaitForAction)
        Assert.assertNull(game.gameState.boardState.shamanAt(Pos(3, 0)))
        Assert.assertNotNull(game.gameState.boardState.monolithAt(Pos(3, 0)))
    }

    @Test
    fun `when move allows for surrounded transformation it removes the enemy shaman and spawn a monolith`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(0, 0))
            positionSeaShaman(Pos(2, 0))
            positionForestShaman(Pos(3, 0))
            moveAction(MoveActionTile.Orthogonally)
            transformation(TransformationTile.Surrounded)
        }
        val s1 = game.gameState.boardState.shamanAt(Pos(0, 0))!!
        val newMonolith = game.gameState.boardState.monolithsStack.first()

        game.apply {
            perform(MoveShaman(s1, Team.Forest, Pos(1, 0)))
            perform(
                TransformShaman(
                    game.gameState.boardState.shamanAt(Pos(1, 0))!!,
                    game.gameState.boardState.shamanAt(Pos(3, 0))!!,
                    game.gameState.boardState.shamanAt(Pos(2, 0))!!,
                )
            )
            perform(SelectMonolith(newMonolith))
        }

        Assert.assertTrue(game.gameState is WaitForAction)
        Assert.assertNull(game.gameState.boardState.shamanAt(Pos(2, 0)))
        Assert.assertEquals(game.gameState.boardState.monolithAt(Pos(2, 0))?.type, newMonolith)
    }

    @Test
    fun `one of shamans in a transformation needs to be have moved this turn`() {
        val game = game {
            emptyBoard()
            positionForestShaman(Pos(0, 0))
            positionForestShaman(Pos(1, 0))
            positionForestShaman(Pos(2, 0))
            positionSeaShaman(Pos(3, 0))
            moveAction(MoveActionTile.Orthogonally)
            transformation(TransformationTile.Outnumbered)
        }
        val s1 = game.gameState.boardState.shamanAt(Pos(0, 0))!!

        game.perform(MoveShaman(s1, Team.Forest, Pos(0, 1)))
        val result = game.perform(
            TransformShaman(
                game.gameState.boardState.shamanAt(Pos(1, 0))!!,
                game.gameState.boardState.shamanAt(Pos(2, 0))!!,
                game.gameState.boardState.shamanAt(Pos(3, 0))!!,
            )
        )

        Assert.assertFalse(result)
        Assert.assertTrue(game.gameState is WaitForTransformation)
    }

    @Test
    fun `after a spawn action a transformation may be performed`() {
        val spawn = SpawnActionTile.White
        val game = game {
            emptyBoard()
            addInactiveShamans()
            positionForestShaman(Pos(2, 0))
            positionSeaShaman(Pos(3, 0))
            spawnAction(spawn)
            transformation(TransformationTile.Outnumbered)
            activeTeam = Team.Forest
        }
        val newMonolith = game.gameState.boardState.monolithsStack.first()

        game.apply {
            Assert.assertEquals(spawn.forest, Pos(1, 0))
            perform(
                SpawnShaman(Team.Forest, spawn.forest)
            )
            perform(
                TransformShaman(
                    game.gameState.boardState.shamanAt(Pos(1, 0))!!,
                    game.gameState.boardState.shamanAt(Pos(2, 0))!!,
                    game.gameState.boardState.shamanAt(Pos(3, 0))!!,
                )
            )
            perform(SelectMonolith(newMonolith))
        }

        Assert.assertTrue(game.gameState is WaitForAction)
        Assert.assertNull(game.gameState.boardState.shamanAt(Pos(3, 0)))
        Assert.assertEquals(game.gameState.boardState.monolithAt(Pos(3, 0))?.type, newMonolith)
    }
}
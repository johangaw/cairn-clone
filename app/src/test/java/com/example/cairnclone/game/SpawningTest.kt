package com.example.cairnclone.game

import com.example.cairnclone.game.actions.SpawnShaman
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.SpawnActionTile
import com.example.cairnclone.game.board.Team
import com.example.cairnclone.game.states.WaitForTransformation
import com.example.cairnclone.game.states.monoliths.ActivatingChaosOfTheGiants
import org.junit.Assert.*
import org.junit.Test

class SpawningTest {

    @Test
    fun `when spawning on a none spawning tile, it returns false`() {
        val team = Team.Forest
        val game = game {
            emptyBoard()
            activeTeam = team
            addInactiveShamans()
        }
        val result = game.perform(SpawnShaman(team, Pos(3, 3)))
        assertFalse(result)
    }

    @Test
    fun `when spawning on a inactive spawning tile, it returns false`() {
        val team = Team.Forest
        val game = game {
            emptyBoard()
            addInactiveShamans()
            spawnAction = SpawnActionTile.Black
            activeTeam = team
        }
        val result = game.perform(SpawnShaman(team, SpawnActionTile.White.forest))
        assertFalse(result)
    }

    @Test
    fun `when spawning on the other teams spawning tile, it returns false`() {
        val team = Team.Forest
        val game = game {
            emptyBoard()
            addInactiveShamans()
            activeTeam = team
            spawnAction = SpawnActionTile.Black
        }
        val result = game.perform(SpawnShaman(team, SpawnActionTile.Black.sea))
        assertFalse(result)
    }

    @Test
    fun `when there are no inactive shamans, it returns false`() {
        val team = Team.Forest
        val game = game {
            emptyBoard()
            spawnAction = SpawnActionTile.White
            activeTeam = team
            repeat(5) {
                positionForestShaman(Pos(1, it))
            }
        }
        val result = game.perform(SpawnShaman(team, SpawnActionTile.White.forest))
        assertFalse(result)
    }

    @Test
    fun `when the spawn position contains a team mate, it returns false`() {
        val team = Team.Forest
        val spawn = SpawnActionTile.White
        val game = game {
            emptyBoard()
            spawnAction = spawn
            activeTeam = team
            positionForestShaman(spawn.forest)
            addInactiveShamans()
        }
        val result = game.perform(SpawnShaman(team, spawn.forest))
        assertFalse(result)
    }

    @Test
    fun `when spawning on a valid spawning tile, it returns true, adds shaman to that position and wait for a transformation`() {
        val team = Team.Forest
        val spawn = SpawnActionTile.Black
        val game = game {
            emptyBoard()
            spawnAction = spawn
            activeTeam = team
            addInactiveShamans()
        }
        val result = game.perform(SpawnShaman(team, spawn.forest))

        assertTrue(result)
        assertNotNull(game.boardState.shamanAt(spawn.forest))
        assertTrue(game.gameState is WaitForTransformation)
        assertEquals(game.boardState.spawnActionTile, SpawnActionTile.White)
    }

    @Test
    fun `when the spawning tile contains an opposing shaman, it returns true and replace the opposing shaman with one of your inactive ones`() {
        val team = Team.Forest
        val spawn = SpawnActionTile.Black
        val game = game {
            emptyBoard()
            spawnAction = spawn
            activeTeam = team
            positionSeaShaman(spawn.forest)
            addInactiveShamans()
        }
        val result = game.perform(SpawnShaman(team, spawn.forest))

        assertTrue(result)
        assertEquals(game.boardState.shamanAt(spawn.forest)?.team, Team.Forest)
        assertEquals(game.boardState.activeShamans.count { it.team == Team.Sea }, 0)
    }

    @Test
    fun `when spawning a shaman onto a monolith it activates that monolith`() {
        val spawn = SpawnActionTile.White
        val game = game {
            emptyBoard()
            addInactiveShamans()
            positionMonolith(MonolithType.ChaosOfTheGiants, spawn.forest)
            activeTeam = Team.Forest
            spawnAction = spawn
        }
        val result = game.perform(SpawnShaman(Team.Forest, spawn.forest))

        assertTrue(result)
        assertTrue("not correct class ${game.boardState.javaClass.simpleName}", game.gameState is ActivatingChaosOfTheGiants)
    }
}
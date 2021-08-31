package com.example.cairnclone

import com.example.cairnclone.game.*
import org.junit.Test
import org.junit.Assert.*

class PossibleTransformationTest {

    @Test
    fun `it returns null when the first two shamans are on different teams`() {
        val game = Game()
        val transformation = game.possibleTransformation(
            Shaman(team = Team.Forest, pos = Pos(0, 0)),
            Shaman(team = Team.Sea, pos = Pos(0, 0)),
            Shaman(team = Team.Forest, pos = Pos(0, 0)),
        )

        assertEquals(transformation, null)
    }

    @Test
    fun `it returns null when the enemy shaman is on the same team as the others`() {
        val game = Game()
        val transformation = game.possibleTransformation(
            Shaman(team = Team.Forest, pos = Pos(0, 0)),
            Shaman(team = Team.Forest, pos = Pos(0, 0)),
            Shaman(team = Team.Forest, pos = Pos(0, 0)),
        )

        assertEquals(transformation, null)
    }


    @Test
    fun `it returns Surrounded when the enemy shaman is surrounded by the other team and Surrounded is active`() {
        val shamans = arrayOf(
            Shaman(team = Team.Forest, pos = Pos(1, 1)),
            Shaman(team = Team.Forest, pos = Pos(3, 3)),
            Shaman(team = Team.Sea, pos = Pos(2, 2)),
        )
        val game = Game(shamans = setOf(*shamans), transformation = Transformation.Surrounded)
        val transformation = game.possibleTransformation(shamans[0], shamans[1], shamans[2])

        assertEquals(transformation, Transformation.Surrounded)
    }

    @Test
    fun `it returns null when the enemy shaman is not surrounded by the other team and Surrounded is active`() {
        val shamans = arrayOf(
            Shaman(team = Team.Forest, pos = Pos(1, 1)),
            Shaman(team = Team.Forest, pos = Pos(3, 2)),
            Shaman(team = Team.Sea, pos = Pos(2, 2)),
        )
        val game = Game(shamans = setOf(*shamans), transformation = Transformation.Surrounded)
        val transformation = game.possibleTransformation(shamans[0], shamans[1], shamans[2])

        assertNull(transformation)
    }

    @Test
    fun `it returns null when the enemy shaman is surrounded by the other team and Outnumbered is active`() {
        val shamans = arrayOf(
            Shaman(team = Team.Forest, pos = Pos(1, 1)),
            Shaman(team = Team.Forest, pos = Pos(3, 2)),
            Shaman(team = Team.Sea, pos = Pos(2, 2)),
        )
        val game = Game(shamans = setOf(*shamans), transformation = Transformation.Outnumbered)
        val transformation = game.possibleTransformation(shamans[0], shamans[1], shamans[2])

        assertNull(transformation)
    }

    @Test
    fun `it returns Outnumbered when the enemy shaman is outnumbered by the other team and Outnumbered is active`() {
        val shamans = arrayOf(
            Shaman(team = Team.Forest, pos = Pos(1, 2)),
            Shaman(team = Team.Forest, pos = Pos(1, 1)),
            Shaman(team = Team.Sea, pos = Pos(1, 3)),
        )
        val game = Game(shamans = setOf(*shamans), transformation = Transformation.Outnumbered)
        val transformation = game.possibleTransformation(shamans[0], shamans[1], shamans[2])

        assertEquals(transformation, Transformation.Outnumbered)
    }
}
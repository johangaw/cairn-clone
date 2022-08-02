package com.example.cairnclone

import com.example.cairnclone.game_old.Direction
import com.example.cairnclone.game_old.Pos
import com.example.cairnclone.game_old.adjacentDirection
import com.example.cairnclone.game_old.plus
import org.junit.Test
import org.junit.Assert.*


class AdjacentDirectionTest {

    @Test
    fun `it returns null if the Positions are not next to each other`() {
        assertNull(Pos(1,1).adjacentDirection(Pos(3,3)))
        assertNull(Pos(1,1).adjacentDirection(Pos(1,3)))
        assertNull(Pos(3,3).adjacentDirection(Pos(1,3)))
    }

    @Test
    fun `when the Positions are next to each other the relation looks like pos + dir = pother`() {
        val pos = Pos(2,2)
        Direction.values().forEach {dir ->
            val other = pos + dir
            assertEquals(pos.adjacentDirection(other), dir)
        }
    }
}
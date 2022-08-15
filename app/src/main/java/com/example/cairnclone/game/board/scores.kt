package com.example.cairnclone.game.board

data class Scores(val seaTeam: Score, val forestTeam: Score)

@JvmInline
value class Score(val value: Int) {
    init {
        require(value in 0..3)
    }
}
package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.Monolith
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.states.NextState

interface MonolithGameState {
    val monolith: Monolith
    val shaman: Shaman
    val nextState: NextState
    fun canActivate(): Boolean
    fun isValid(monolithType: MonolithType) =
        monolith.type == monolithType && shaman.pos == monolith.pos
}
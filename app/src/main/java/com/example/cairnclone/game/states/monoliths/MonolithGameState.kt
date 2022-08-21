package com.example.cairnclone.game.states.monoliths

import com.example.cairnclone.game.MonolithType

interface MonolithGameState {
    val monolith: MonolithType
    fun canActivate(): Boolean
}
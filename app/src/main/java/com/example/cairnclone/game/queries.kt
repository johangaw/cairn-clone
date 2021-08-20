package com.example.cairnclone.game

fun Game.shamanAt(pos: Pos): Shaman? {
    return this.shamans.firstOrNull { it.pos == pos }
}

fun Game.shamanAt(x: Int, y: Int): Shaman? = this.shamanAt(Pos(x,y))
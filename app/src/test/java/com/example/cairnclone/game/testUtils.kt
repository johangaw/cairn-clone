package com.example.cairnclone.game

import com.example.cairnclone.game.board.BoardStateBuilder
import com.example.cairnclone.game.board.buildBoard
import com.example.cairnclone.game.states.WaitForAction

fun game(init: BoardStateBuilder.() -> Unit) = Game(
    WaitForAction(buildBoard(init)),
    {}
)
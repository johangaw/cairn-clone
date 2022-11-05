package com.example.cairnclone.game.actions

import com.example.cairnclone.game.board.MonolithType
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.board.Team

interface Action

data class SpawnShaman(val team: Team, val pos: Pos): Action

data class MoveShaman(val shaman: Shaman, val team: Team, val newPos: Pos): Action

data class JumpOverShaman(val jumper: Shaman, val newPos: Pos): Action

data class TransformShaman(val trans1: Shaman, val trans2: Shaman, val target: Shaman): Action

data class SelectMonolith(val monolithType: MonolithType): Action

object EndTurn : Action


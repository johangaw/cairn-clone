package com.example.cairnclone.game.actions

import com.example.cairnclone.game.*

interface Action

data class SpawnShaman(val shaman: Shaman): Action

data class MoveShaman(val shaman: Shaman, val team: Team, val newPos: Pos): Action

data class TransformShaman(val trans1: Shaman,val trans2: Shaman, val target: Shaman): Action

data class SelectMonolith(val monolithType: MonolithType): Action

object EndTurn : Action


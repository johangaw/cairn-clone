package com.example.cairnclone.game.actions

import com.example.cairnclone.game.Pos
import com.example.cairnclone.game.Shaman
import com.example.cairnclone.game.ShamanId
import com.example.cairnclone.game.Team

interface Action

data class SpawnShaman(val shaman: Shaman): Action

data class MoveShaman(val shamanId: ShamanId, val team: Team, val newPos: Pos): Action

data class TransformShaman(val trans1: ShamanId,val trans2: ShamanId, val target: ShamanId): Action

data class BuildMonolith(val pos: Pos, val team: Team) : Action

object EndTurn : Action


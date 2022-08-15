package com.example.cairnclone.game.board

import java.util.*

@JvmInline
value class ShamanId(private val id: UUID = UUID.randomUUID())

data class Shaman(
    val id: ShamanId = ShamanId(),
    val team: Team,
    val pos: Pos,
)
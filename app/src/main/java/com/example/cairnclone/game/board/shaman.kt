package com.example.cairnclone.game.board

import java.util.*

@JvmInline
value class ShamanId(private val id: UUID = UUID.randomUUID())

data class Shaman(
    val id: ShamanId = ShamanId(),
    val team: Team,
    val pos: Pos,
)

data class InactiveShaman(val id: ShamanId = ShamanId(), val team: Team)

fun Shaman.toInactiveShaman(): InactiveShaman = InactiveShaman(id, team)
fun InactiveShaman.toShaman(pos: Pos): Shaman = Shaman(id, team, pos)
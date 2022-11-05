package com.example.cairnclone.ui

import com.example.cairnclone.game.board.Monolith
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.board.Team

fun ensureOneShaman(shamans: Set<Shaman>) =
    if (shamans.size != 1) throw Exception("The monolith requires ONE shamans") else shamans.first()

fun ensureOnePos(positions: Set<Pos>) =
    if (positions.size != 1) throw Exception("The monolith requires ONE position") else positions.first()

fun ensureThreeShamans(shamans: Set<Shaman>) =
    if (shamans.size != 3) throw Exception("A transformation requires THREE shamans") else shamans

fun ensureTwoTeams(shamans: Set<Shaman>) =
    if (shamans.all { s -> s.team == Team.Forest } || shamans.all { s -> s.team == Team.Sea }) throw Exception(
        "A transformation requires shamans of different teams"
    ) else shamans

fun <T> ensureNotNull(nullable: T?) = nullable ?: throw Exception("Nothing there")

fun <T>ensureOne(set: Set<T>) =
    if (set.size != 1) throw Exception("The monolith requires ONE selected") else set.first()


fun ensureShaman(data: DADData): Shaman = if(data is DADData.Shaman) data.shaman else throw Exception("Need to move a shaman")

fun ensureMonolith(data: DADData): Monolith = if(data is DADData.Monolith) data.monolith else throw Exception("Need to move a monolith")

fun orderShamansAsTransformationArguments(shamans: Set<Shaman>) =
    shamans.partition { s -> s.team == Team.Sea }
        .toList()
        .sortedByDescending { list -> list.size }
        .flatten()
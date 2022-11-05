package com.example.cairnclone.ui

import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.board.Team

fun ensureTwoDifferentTeams(shamans: Set<Shaman>) =
    if (shamans.all { s -> s.team == Team.Forest } || shamans.all { s -> s.team == Team.Sea }) throw Exception(
        "A transformation requires shamans of different teams"
    ) else shamans

fun <T> ensureNotNull(nullable: T?) = nullable ?: throw Exception("Nothing there")

fun <T> ensureCount(count: Int) = fun (set: Set<T>) =
    if (set.size != count) throw Exception("The monolith requires $count selected") else set

fun <T> ensureOne(set: Set<T>) = ensureCount<T>(1)(set).first()

fun orderShamansAsTransformationArguments(shamans: Set<Shaman>) =
    shamans.partition { s -> s.team == Team.Sea }
        .toList()
        .sortedByDescending { list -> list.size }
        .flatten()
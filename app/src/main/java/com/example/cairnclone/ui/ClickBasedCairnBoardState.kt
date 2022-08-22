package com.example.cairnclone.ui

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.cairnclone.game.Game
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.*
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.states.monoliths.*

fun ensureOneShamans(shamans: Set<Shaman>) =
    if (shamans.size != 1) throw Exception("The monolith requires ONE shamans") else shamans

fun ensureOnePos(positions: Set<Pos>) =
    if (positions.size != 1) throw Exception("The monolith requires ONE position") else positions

fun ensureThreeShamans(shamans: Set<Shaman>) =
    if (shamans.size != 3) throw Exception("A transformation requires THREE shamans") else shamans

fun ensureTwoTeams(shamans: Set<Shaman>) =
    if (shamans.all { s -> s.team == Team.Forest } || shamans.all { s -> s.team == Team.Sea }) throw Exception(
        "A transformation requires shamans of different teams"
    ) else shamans

fun <T>ensureNotNull(nullable: T?) = nullable ?: throw Exception("Nothing there")

fun orderShamansAsTransformationArguments(shamans: Set<Shaman>) =
    shamans.partition { s -> s.team == Team.Sea }
        .toList()
        .sortedByDescending { list -> list.size }
        .flatten()


class ClickBasedCairnBoardState(private val emitter: (Action) -> Boolean, val showError: (msg: String) -> Unit) {
    private var shamans by mutableStateOf<Set<Shaman>>(emptySet())
    private var positions by mutableStateOf<Set<Pos>>(emptySet())
    private var monlith by mutableStateOf<MonolithType?>(null)

    val selectedShamans: Set<Shaman> get() = shamans
    val selectedPositions: Set<Pos> get() = positions
    val selectedMonolith: MonolithType? get() = monlith

    private fun toggleShaman(id: Shaman) {
        shamans =
            if (id in shamans) shamans - id
            else shamans + id
    }

    private fun resetSelection() {
        shamans = emptySet()
        positions = emptySet()
        monlith = null
    }

    private fun showError(err: Throwable) = err.message?.let { showError(it) }

    fun handleBoardClick(pos: Pos, state: BoardState) {
        val shaman = state.shamanAt(pos)
        positions = if (pos in positions) positions - pos else positions + pos

        if (shaman != null) {
            toggleShaman(shaman)
        } else if (shamans.size == 1) {
            val selectedShaman = shamans.first()
            if (selectedShaman.pos.isAdjacent(pos))
                emitter(MoveShaman(selectedShaman, state.activeTeam, pos))
            else
                emitter(JumpOverShaman(selectedShaman, pos))
            resetSelection()
        } else if (selectedShamans.size > 1) {
            showError("Can only move ONE shaman at once")
        } else if (selectedShamans.isEmpty()
            && state.spawnActionTile.posFor(state.activeTeam) == pos
        ) {
            emitter(SpawnShaman(state.activeTeam, pos))
            resetSelection()
        }
    }

    fun handleVillageClick(team: Team, state: BoardState): () -> Unit = {
        Result.success(selectedShamans)
            .mapCatching(::ensureOneShamans)
            .onSuccess {
                val shaman = selectedShamans.first()
                state.board.villageRowFor(team)
                    .firstOrNull { emitter(MoveShaman(shaman, state.activeTeam, it)) }
                resetSelection()
            }
            .onFailure(::showError)
    }

    fun handleUpcomingMonolithClick(monolith: MonolithType) {
        emitter(SelectMonolith(monolith))
        resetSelection()
    }

    fun handleTransformClick() {
        Result.success(selectedShamans)
            .mapCatching(::ensureThreeShamans)
            .mapCatching(::ensureTwoTeams)
            .map(::orderShamansAsTransformationArguments)
            .onSuccess {
                val (s1, s2, target) = it
                emitter(TransformShaman(s1, s2, target))
                resetSelection()
            }
            .onFailure(::showError)
    }

    fun handleEndTurnClick() {
        emitter(EndTurn)
        resetSelection()
    }

    fun handleActivateMonolith(monolith: MonolithType, boardState: BoardState) {
        when (monolith) {
            MonolithType.ChaosOfTheGiants -> Result.success(shamans)
                .mapCatching(::ensureOneShamans)
                .onSuccess { emitter(ActivatingChaosOfTheGiants.Activate(shamans.first())) }
                .onFailure(::showError)
            MonolithType.CairnOfDawn -> Result.success(positions)
                .mapCatching(::ensureOnePos)
                .onSuccess { emitter(ActivatingCairnOfDawn.Activate(positions.first())) }
                .onFailure(::showError)
            MonolithType.CromlechOfTheStars -> Result.success(positions)
                .mapCatching(::ensureOnePos)
                .map { boardState.monolithAt(it.first()) }
                .mapCatching(::ensureNotNull)
                .onSuccess { emitter(ActivatingCromlechOfTheStars.MoveToMonolith(it)) }
                .onFailure(::showError)
            MonolithType.PillarsOfSpring -> emitter(ActivatingPillarsOfSpring.MakeNextTurnMyTurn)
            MonolithType.AlleyOfDusk -> Result.success(shamans)
                .mapCatching(::ensureOneShamans)
                .onSuccess { emitter(ActivatingAlleyOfDusk.BanishShaman(it.first())) }
                .onFailure(::showError)
            else -> throw NotImplementedError("Activate not implemented for ${monolith.name}")
        }
        resetSelection()
    }

    fun showMonolithInfo(type: MonolithType) {
        monlith = type
    }

    fun hideMonolithInfo() {
        monlith = null
    }
}

@Composable
fun rememberClickBasedCairnBoardState(game: Game): ClickBasedCairnBoardState {
    val context = LocalContext.current
    return remember {
        ClickBasedCairnBoardState({ game.perform(it) }) { msg ->
            Toast.makeText(
                context,
                msg,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}


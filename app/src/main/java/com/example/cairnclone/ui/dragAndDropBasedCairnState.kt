package com.example.cairnclone.ui

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.cairnclone.game.Game
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.actions.*
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.game.board.Team
import com.example.cairnclone.game.board.isAdjacent
import com.example.cairnclone.game.states.monoliths.*
import com.example.cairnclone.ui.draganddrop.DragAndDropContext
import com.example.cairnclone.game.Monolith as DomainMonolith
import com.example.cairnclone.game.board.Shaman as DomainShaman

sealed class DADData {
    data class Shaman(val shaman: DomainShaman) : DADData()
    data class Monolith(val monolith: DomainMonolith) : DADData()
}

val LocalCairnBoardDADContext = compositionLocalOf { DragAndDropContext<DADData>() }

private fun <T> Set<T>.toggle(value: T) = if (this.contains(value)) this - value else this + value

data class DADBasedCairnBoardState(
    private val emitter: (Action) -> Boolean,
    val showError: (msg: String) -> Unit
) {

    private var _selectedShamans by mutableStateOf<Set<DomainShaman>>(emptySet())
    val selectedShamans get(): Set<DomainShaman> = _selectedShamans

    private var _selectedPositions by mutableStateOf<Set<Pos>>(emptySet())
    val selectedPositions get(): Set<Pos> = _selectedPositions

    private var _selectedMonolith by mutableStateOf<DomainMonolith?>(null)
    val selectedMonolith get(): DomainMonolith? = _selectedMonolith

    private fun resetSelection() {
        _selectedShamans = emptySet()
        _selectedPositions = emptySet()
        _selectedMonolith = null
    }

    fun handleEndTurnClick() {
        emitter(EndTurn)
    }

    fun handleBoardClick(pos: Pos, state: BoardState, stage: GameStage) {
        when (stage) {
            GameStage.Action -> handleActionPhaseClick(pos, state)
            GameStage.Transformation -> handleTransformationPhaseClick(pos, state)
            is GameStage.ActivatingMonolith -> handleActivatingMonolithPhaseClick(
                pos,
                state,
                stage.monolith
            )
            GameStage.SelectMonolith, GameStage.End -> {}
        }
    }

    private fun handleActivatingMonolithPhaseClick(
        pos: Pos,
        state: BoardState,
        monolith: MonolithType
    ) {
        when (monolith) {
            MonolithType.AlleyOfDusk,
            MonolithType.ChaosOfTheGiants -> state.shamanAt(pos)
                ?.let { _selectedShamans = _selectedShamans.toggle(it) }
            else -> _selectedPositions = _selectedPositions.toggle(pos)
        }

    }

    private fun handleTransformationPhaseClick(pos: Pos, state: BoardState) {
        val shaman = state.shamanAt(pos) ?: return
        _selectedShamans = _selectedShamans.toggle(shaman)
    }

    private fun handleActionPhaseClick(pos: Pos, state: BoardState) {
        emitter(SpawnShaman(state.activeTeam, pos))
    }

    fun handleMoveToVillage(data: DADData, villageTeam: Team, state: BoardState, stage: GameStage) {
        when (stage) {
            is GameStage.ActivatingMonolith,
            GameStage.Action -> handleMoveToVillageInActionOrActivatingMonolithPhase(data, villageTeam, state)
            GameStage.End, GameStage.SelectMonolith, GameStage.Transformation -> {}
        }
    }

    private fun handleMoveToVillageInActionOrActivatingMonolithPhase(data: DADData, villageTeam: Team, state: BoardState) {
        when (data) {
            is DADData.Shaman -> {
                state.board.villageRowFor(villageTeam)
                    .firstOrNull { emitter(MoveShaman(data.shaman, data.shaman.team, it)) }
                    ?: state.board.villageRowFor(villageTeam)
                        .firstOrNull { emitter(JumpOverShaman(data.shaman, it)) }

            }
            is DADData.Monolith -> {}
        }
    }

    fun handleMoveToPos(data: DADData, target: Pos, state: BoardState, stage: GameStage) {
        when (stage) {
            GameStage.Action -> handleMoveToPosInActionStage(data, target)
            is GameStage.ActivatingMonolith -> handleMoveToPosInActivatingMonolithPhase(
                data,
                target,
                state,
                stage.monolith
            )
            GameStage.Transformation, GameStage.SelectMonolith, GameStage.End -> {}
        }
    }

    private fun handleMoveToPosInActivatingMonolithPhase(
        data: DADData,
        target: Pos,
        boardState: BoardState,
        monolith: MonolithType
    ) {
        when (monolith) {
            MonolithType.CromlechOfTheStars -> Result.success(data)
                .mapCatching(::ensureShaman)
                .map { boardState.monolithAt(target) }
                .mapCatching(::ensureNotNull)
                .onSuccess { emitter(ActivatingCromlechOfTheStars.MoveToMonolith(it)) }
                .onFailure(::showError)
            MonolithType.DeerRock -> Result.success(data)
                .mapCatching(::ensureShaman)
                .onSuccess { shaman -> emitter(ActivatingDeerRock.MoveShaman(shaman, target)) }
                .onFailure(::showError)
            MonolithType.MenhirOfTheDancers -> Result.success(data)
                .mapCatching(::ensureShaman)
                .onSuccess { emitter(ActivatingMenhirOfTheDancers.MoveShaman(target)) }
                .onFailure(::showError)

            else -> throw NotImplementedError("Move to pos not implemented for ${monolith.name}")
        }
    }

    private fun handleMoveToPosInActionStage(data: DADData, target: Pos) {
        when (data) {
            is DADData.Shaman -> emitter(
                if (data.shaman.pos.isAdjacent(target)) MoveShaman(
                    data.shaman,
                    data.shaman.team,
                    target
                )
                else JumpOverShaman(data.shaman, target)
            )
            is DADData.Monolith -> TODO()
        }
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

    private fun showError(err: Throwable) = err.message?.let { showError(it) }

    fun handleUpcomingMonolithClick(it: MonolithType) {
        emitter(SelectMonolith(it))
    }

    fun handleActivateMonolith(monolith: MonolithType, boardState: BoardState) {
        when (monolith) {
            MonolithType.ChaosOfTheGiants -> Result.success(selectedShamans)
                .mapCatching(::ensureOneShaman)
                .onSuccess { emitter(ActivatingChaosOfTheGiants.Activate(it)) }
                .onFailure(::showError)
            MonolithType.CairnOfDawn -> Result.success(selectedPositions)
                .mapCatching(::ensureOnePos)
                .onSuccess { emitter(ActivatingCairnOfDawn.Activate(it)) }
                .onFailure(::showError)
            MonolithType.PillarsOfSpring -> emitter(ActivatingPillarsOfSpring.MakeNextTurnMyTurn)
            MonolithType.AlleyOfDusk -> Result.success(selectedShamans)
                .mapCatching(::ensureOneShaman)
                .onSuccess { emitter(ActivatingAlleyOfDusk.BanishShaman(it)) }
                .onFailure(::showError)
            MonolithType.SanctuaryOfTheAges -> Result.success(selectedPositions)
                .mapCatching(::ensureOnePos)
                .onSuccess { emitter(ActivatingSanctuaryOfTheAges.MoveMonolith(it)) }
                .onFailure(::showError)
            else -> throw NotImplementedError("Activate not implemented for ${monolith.name}")
        }
        resetSelection()
    }

    fun handleLongBoardClick(pos: Pos, state: BoardState, stage: GameStage) {
        val monolith = state.monolithAt(pos) ?: return
        _selectedMonolith = monolith
    }

    fun unselectMonolith() {
        _selectedMonolith = null
    }
}

@Composable
fun rememberDADBasedCairnBoardState(game: Game): DADBasedCairnBoardState {
    val context = LocalContext.current
    return remember {
        DADBasedCairnBoardState({ game.perform(it) }) { msg ->
            Toast.makeText(
                context,
                msg,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
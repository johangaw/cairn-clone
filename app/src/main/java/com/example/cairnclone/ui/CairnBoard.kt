package com.example.cairnclone.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cairnclone.R
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.*

private val SpawnActionTile.positions: List<Pos> get() = listOf(this.forest, this.sea)

sealed class GameStage {
    object Action : GameStage()
    object SelectMonolith : GameStage()
    object Transformation : GameStage()
    data class ActivatingMonolith(val monolith: MonolithType) : GameStage()
    object End : GameStage()
}

fun IntRange.allPairs(other: IntRange): List<Pair<Int, Int>> =
    this.flatMap { left -> other.map { right -> Pair(left, right) } }

fun ensureOneShamans(shamans: Set<Shaman>) =
    if (shamans.size != 1) throw Exception("The monolith requires ONE shamans") else shamans

fun ensureThreeShamans(shamans: Set<Shaman>) =
    if (shamans.size != 3) throw Exception("A transformation requires THREE shamans") else shamans

fun ensureTwoTeams(shamans: Set<Shaman>) =
    if (shamans.all { s -> s.team == Team.Forest } || shamans.all { s -> s.team == Team.Sea }) throw Exception(
        "A transformation requires shamans of different teams"
    ) else shamans

fun orderShamansAsTransformationArguments(shamans: Set<Shaman>) =
    shamans.partition { s -> s.team == Team.Sea }
        .toList()
        .sortedByDescending { list -> list.size }
        .flatten()

fun showError(ctx: Context) =
    { err: Throwable -> Toast.makeText(ctx, err.message, Toast.LENGTH_LONG).show() }


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CairnBoard(
    state: BoardState,
    stage: GameStage,
    performMove: (shaman: Shaman, newPos: Pos) -> Boolean,
    performJump: (shaman: Shaman, newPos: Pos) -> Boolean,
    performSpawn: (pos: Pos) -> Boolean,
    performEndTurn: () -> Boolean,
    performTransformation: (s1: Shaman, s2: Shaman, target: Shaman) -> Boolean,
    performSelectMonolith: (monolith: MonolithType) -> Boolean,
    activateChaosOfTheGiants: (shaman: Shaman) -> Boolean,
    skipChaosOfTheGiants: () -> Boolean,
) {
    var selectedShamans by remember { mutableStateOf(emptySet<Shaman>()) }
    val context = LocalContext.current

    fun onVillageClick(team: Team): () -> Unit = {
        Result.success(selectedShamans)
            .mapCatching(::ensureOneShamans)
            .onSuccess {
                val shaman = selectedShamans.first()
                state.board.villageRowFor(team).firstOrNull { performMove(shaman, it) }
                selectedShamans = emptySet()
            }
            .onFailure(showError(context))
    }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Village(Team.Forest, state.activeTeam == Team.Forest, state.scores.forestTeam ,onVillageClick(Team.Forest))
        Column {

            LazyVerticalGrid(
                cells = GridCells.Fixed(state.board.width),
                Modifier.padding(horizontal = 24.dp)
            ) {
                val positions = (0 until state.board.height)
                    .allPairs((0 until state.board.width))
                    .map { (y, x) -> Pos(x, y) }

                items(positions) { pos ->
                    val shaman = state.shamanAt(pos)
                    val monolith = state.monolithAt(pos)
                    val pieceType = when {
                        SpawnActionTile.Black.positions.contains(pos) -> BoardPieceType.BlackSpawn
                        SpawnActionTile.White.positions.contains(pos) -> BoardPieceType.WhiteSpawn
                        else -> BoardPieceType.Normal
                    }
                    BoardPiece(
                        type = pieceType,
                        onClick = {
                            if (shaman != null) {
                                selectedShamans =
                                    if (selectedShamans.contains(shaman))
                                        selectedShamans - shaman
                                    else
                                        selectedShamans + shaman
                            } else if (selectedShamans.size == 1) {
                                val selectedShaman = selectedShamans.first()
                                val isAdjacent = selectedShaman.pos.adjacentDirection(pos) != null
                                if(isAdjacent)
                                    performMove(selectedShaman, pos)
                                else
                                    performJump(selectedShaman, pos)
                                selectedShamans = emptySet()
                            } else if (selectedShamans.size > 1) {
                                Toast.makeText(
                                    context,
                                    "Can only move ONE shaman at once",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (selectedShamans.isEmpty() && state.spawnActionTile.posFor(
                                    state.activeTeam
                                ) == pos
                            ) {
                                performSpawn(pos)
                            }
                        }
                    ) {
                        monolith?.let {
                            MonolithPiece(monolith.type)
                        }
                        shaman?.let {
                            ShamanPiece(
                                shaman,
                                selected = selectedShamans.contains(shaman)
                            )
                        }
                    }
                }
            }
        }
        Village(Team.Sea, state.activeTeam == Team.Sea, state.scores.seaTeam, onVillageClick(Team.Sea))

        Divider(thickness = 4.dp, color = Color.Black, modifier = Modifier.padding(8.dp, 8.dp))

        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceAround,
            Alignment.CenterVertically
        ) {
            SpawnTilePiece(state.spawnActionTile)
            MovementTilePiece(state.moveActionTile)
            JumpTilePiece(state.jumpActionTile)
            TransformationTilePiece(state.transformationTile)
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (stage == GameStage.SelectMonolith)
                UpcomingMonoliths(
                    state.upcomingMonoliths,
                    { performSelectMonolith(it) },
                )

            if (stage == GameStage.Transformation)
                TransformButton(
                    onClick = {
                        Result.success(selectedShamans)
                            .mapCatching(::ensureThreeShamans)
                            .mapCatching(::ensureTwoTeams)
                            .map(::orderShamansAsTransformationArguments)
                            .onSuccess {
                                val (s1, s2, target) = it
                                performTransformation(s1, s2, target)
                                selectedShamans = emptySet()
                            }
                            .onFailure(showError(context))
                    })

            if (stage == GameStage.Transformation)
                EndRoundButton(
                    onClick = {
                        performEndTurn()
                        selectedShamans = emptySet()
                    })

            if (stage is GameStage.ActivatingMonolith)
                ActivateMonolithButton(onClick = {
                    when (stage.monolith) {
                        MonolithType.ChaosOfTheGiants -> Result.success(selectedShamans)
                            .mapCatching(::ensureOneShamans)
                            .onSuccess { activateChaosOfTheGiants(selectedShamans.first()) }
                            .onFailure(showError(context))
                        else -> Toast.makeText(
                            context,
                            "unknown monolith ${stage.monolith.name}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                })

            if (stage is GameStage.ActivatingMonolith)
                SkipMonolithButton(onClick = {
                    skipChaosOfTheGiants()
                })
        }


    }
}

enum class BoardPieceType(val color: Color) {
    Normal(Color.Gray),
    WhiteSpawn(Color.LightGray),
    BlackSpawn(Color.DarkGray)
}

@Composable
fun BoardPiece(
    onClick: () -> Unit,
    type: BoardPieceType,
    content: @Composable () -> Unit
) {
    Box(
        Modifier
            .size(75.dp)
            .padding(4.dp)
            .background(type.color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun ShamanPiece(
    shaman: Shaman,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val imageId = when (shaman.team) {
        Team.Forest -> R.drawable.forest_shaman
        Team.Sea -> R.drawable.sea_shaman
    }
    Image(
        painterResource(id = imageId),
        contentDescription = "",
        modifier = modifier.graphicsLayer {
            if (selected) {
                scaleX = 1.3f
                scaleY = 1.3f
            }
        })
}

@Composable
fun MonolithPiece(monolithType: MonolithType, onClick: (() -> Unit)? = null) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(
                CircleShape
            )
            .background(Color.Yellow)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Text(text = monolithType.name)
    }
}

@Composable
fun UpcomingMonoliths(
    monoliths: List<MonolithType>,
    onClick: (monolith: MonolithType) -> Unit,
    disabled: Boolean = false
) {
    Row(Modifier.background(Color.LightGray)) {
        monoliths.forEach {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(75.dp)
                    .drawWithContent {
                        drawContent()
                        if (disabled) drawRect(Color.White.copy(alpha = 0.8f))
                    }
                    .padding(4.dp),
            ) {
                MonolithPiece(it, if (disabled) null else ({ onClick(it) }))
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CairnBoardPreview() {
    val state = remember {
        buildBoard {
            emptyBoard()
            positionStartShamans()
            positionStartMonoliths()
        }
    }
    CairnBoard(
        state,
        GameStage.Action,
        { id, pos -> false },
        { id, pos -> false },
        { false },
        { false },
        { s1, s2, s3 -> false },
        { false },
        { false },
        { false },
    )
}
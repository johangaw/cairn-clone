@file:OptIn(ExperimentalFoundationApi::class)

package com.example.cairnclone.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cairnclone.R
import com.example.cairnclone.game.Game
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.states.WaitForAction

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


@Composable
fun CairnBoard(
    state: BoardState,
    stage: GameStage,
    uiState: ClickBasedCairnBoardState,
) {
    val selectedPositions = uiState.selectedPositions
    val selectedShamans = uiState.selectedShamans
    val selectedMonolith = uiState.selectedMonolith

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Village(
            Team.Forest,
            state.activeTeam == Team.Forest,
            state.scores.forestTeam,
            uiState.handleVillageClick(Team.Forest, state)
        )
        Column {
            LazyVerticalGrid(
                cells = GridCells.Fixed(state.board.width),
                Modifier.padding(horizontal = 24.dp)
            ) {
                val positions = (0 until state.board.height)
                    .allPairs((0 until state.board.width))
                    .map { (y, x) -> Pos(x, y) }

                items(positions) { pos ->
                    val pieceType = when {
                        SpawnActionTile.Black.positions.contains(pos) -> BoardPieceType.BlackSpawn
                        SpawnActionTile.White.positions.contains(pos) -> BoardPieceType.WhiteSpawn
                        else -> BoardPieceType.Normal
                    }

                    BoardPiece(
                        type = pieceType,
                        selected = stage is GameStage.ActivatingMonolith && pos in selectedPositions,
                        onClick = { uiState.handleBoardClick(pos, state) }
                    ) {
                        state.monolithAt(pos)?.let {
                            MonolithPiece(
                                monolithType = it.type,
                                onClick = { uiState.handleBoardClick(pos, state) },
                                onLongClick = { uiState.showMonolithInfo(it.type) })
                        }
                        state.shamanAt(pos)?.let {
                            ShamanPiece(
                                it,
                                selected = selectedShamans.contains(state.shamanAt(pos))
                            )
                        }
                    }
                }
            }
        }
        Village(
            Team.Sea,
            state.activeTeam == Team.Sea,
            state.scores.seaTeam,
            uiState.handleVillageClick(Team.Sea, state)
        )

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
                    { uiState.handleUpcomingMonolithClick(it) },
                )

            if (stage == GameStage.Transformation)
                TransformButton(
                    onClick = {
                        uiState.handleTransformClick()
                    })

            if (stage == GameStage.Transformation)
                EndRoundButton(
                    onClick = { uiState.handleEndTurnClick() })

            if (stage is GameStage.ActivatingMonolith)
                ActivateMonolithButton(onClick = {
                    uiState.handleActivateMonolith(stage.monolith)
                })
        }

        if (selectedMonolith != null)
            AlertDialog(
                onDismissRequest = { uiState.hideMonolithInfo() },
                confirmButton = { TextButton({ uiState.hideMonolithInfo() }) { Text("Ok") } },
                title = { Text(selectedMonolith.name) },
                text = { Text(selectedMonolith.description) }
            )
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
    selected: Boolean,
    type: BoardPieceType,
    content: @Composable () -> Unit
) {
    Box(
        Modifier
            .size(75.dp)
            .padding(4.dp)
            .background(if (selected) Color.Red else type.color)
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
fun MonolithPiece(
    monolithType: MonolithType,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(
                CircleShape
            )
            .background(Color.Yellow)
            .combinedClickable(
                enabled = true,
                onClick = { onClick?.invoke() },
                onLongClick = { onLongClick?.invoke() }
            )
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
    val uiState = rememberClickBasedCairnBoardState(Game(WaitForAction(state), {}))
    CairnBoard(
        state,
        GameStage.Action,
        uiState
    )
}
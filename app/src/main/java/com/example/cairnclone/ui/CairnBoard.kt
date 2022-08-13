package com.example.cairnclone.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cairnclone.R
import com.example.cairnclone.game.*

@Composable
fun CairnBoard(
    state: BoardState,
    performMove: (shaman: Shaman, newPos: Pos) -> Boolean,
    performSpawn: () -> Boolean,
    performEndTurn: () -> Boolean,
) {
    var selectedShaman by remember { mutableStateOf<Shaman?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Village(Team.Forest)
        Column {
            repeat(state.board.height) { y ->
                Row {
                    repeat(state.board.width) { x ->
                        val pos = Pos(x, y)
                        val shaman = state.shamanAt(pos)
                        val monolith = state.monolithAt(pos)
                        val pieceType = when {
                            SpawnActionTile.SpawnBlack.positions.contains(pos) -> BoardPieceType.BlackSpawn
                            SpawnActionTile.SpawnWhite.positions.contains(pos) -> BoardPieceType.WhiteSpawn
                            else -> BoardPieceType.Normal
                        }
                        BoardPiece(
                            type = pieceType,
                            onClick = {
                                val currentSelectedShaman = selectedShaman
                                val adjacentSelectedShaman =
                                    currentSelectedShaman?.pos?.adjacentDirection(
                                        pos
                                    ) != null
                                val isSpawnTile = state.spawnActionTile.positions.contains(pos)
                                when {
                                    shaman != null -> selectedShaman =
                                        if (selectedShaman == shaman) null else shaman
                                    currentSelectedShaman != null && adjacentSelectedShaman -> performMove(
                                        currentSelectedShaman,
                                        pos
                                    )
                                    currentSelectedShaman == null && isSpawnTile -> performSpawn()
                                    else -> {}
                                }
                            }
                        ) {
                            monolith?.let { MonolithPiece(monolith.type) }
                            shaman?.let { ShamanPiece(shaman, selected = selectedShaman == shaman) }
                        }
                    }
                }
            }
        }
        Village(Team.Sea)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { performEndTurn() }) {
            Text(text = "End Turn")
        }
    }
}

@Composable
fun Village(team: Team) {
    Box(
        Modifier
            .height(75.dp)
            .fillMaxWidth()
            .padding(4.dp)
            .background(
                if (team == Team.Sea) Color.Blue else Color.Green
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Village", color = Color.White, style = MaterialTheme.typography.h3)
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
fun MonolithPiece(monolithType: MonolithType) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(
                CircleShape
            )
            .background(Color.Yellow)
    ) {
        Text(text = monolithType.name)
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CairnBoardPreview() {
    val state = remember {
        BoardState(
            Team.Sea,
            Board(),
            SpawnActionTile.SpawnWhite,
            MoveActionTile.Orthogonally,
            TransformationTile.Outnumbered,
            listOf(),
            listOf(
                Shaman(team = Team.Forest, pos = Pos(0, 0)),
                Shaman(team = Team.Sea, pos = Pos(4, 4))
            ),
            listOf(
                Monolith(Pos(1, 2), MonolithType.CairnOfDawn),
                Monolith(Pos(3, 2), MonolithType.ChaosOfTheGiants),
            ),
            listOf(),
            listOf(),
            Scores(Score(0), Score(0))
        )
    }
    CairnBoard(
        state,
        { id, pos -> Log.d("Preview", "performMove"); false },
        { Log.d("Preview", "performSpawn"); false },
        { false }
    )
}
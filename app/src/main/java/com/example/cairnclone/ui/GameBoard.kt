package com.example.cairnclone.ui

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cairnclone.R
import com.example.cairnclone.game.*
import kotlin.random.Random

data class MutableOffset(var x: Float, var y: Float)

fun MutableOffset.asOffset() = Offset(this.x, this.y)
fun MutableOffset.assign(other: Offset) {
    this.x = other.x
    this.y = other.y
}

@Composable
fun GameBoard(game: Game, onMoveShaman: (shaman: Shaman, pos: Pos) -> Unit) {
    val tileSize = 75
    var selectedShaman by remember { mutableStateOf<Shaman?>(null) }

    Box() {
        Column() {
            repeat(game.board.height) { y ->
                Row() {
                    repeat(game.board.width) { x ->
                        Box(
                            Modifier
                                .size(tileSize.dp)
                                .padding(2.dp)
                                .clickable {
                                    if (selectedShaman != null) {
                                        onMoveShaman(selectedShaman!!, Pos(x, y))
                                        selectedShaman = null
                                    }
                                }
                                .background(Color.LightGray),
                        )
                    }
                }
            }
        }

        game.shamans.forEach { shaman ->
            key(shaman.id) {
                val offsetX by animateDpAsState((tileSize * shaman.pos.x).dp)
                val offsetY by animateDpAsState((tileSize * shaman.pos.y).dp)

                Box(
                    Modifier
                        .size(tileSize.dp)
                        .padding(2.dp)
                        .offset(offsetX, offsetY),
                    Alignment.Center
                ) {
                    ShamanPiece(
                        shaman,
                        modifier = Modifier
                            .clickable { selectedShaman = shaman }
                            .scale(if (selectedShaman == shaman) 1.3f else 1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ShamanPiece(shaman: Shaman, modifier: Modifier = Modifier) {
    val imageId = when (shaman.team) {
        Team.Forest -> R.drawable.forest_shaman
        Team.Sea -> R.drawable.forest_shaman
    }
    Image(painterResource(id = imageId), contentDescription = "", modifier = modifier)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameBoardPreview() {
    var game by remember {
        mutableStateOf(
            Game(
                shamans = setOf(
                    Shaman(team = Team.Sea, pos = Pos(2, 0)),
                    Shaman(team = Team.Forest, pos = Pos(3, 4))
                )
            )
        )
    }

    Column(Modifier.fillMaxSize()) {
        GameBoard(game = game, onMoveShaman = { shaman, pos -> game = game.move(shaman, pos) })
        Spacer(modifier = Modifier.height(16.dp))
        Surface() {
            Row {
                Button(onClick = {
                    game = game.move(
                        game.shamans.random(),
                        Pos(Random.nextInt(0, 5), Random.nextInt(0, 5))
                    )
                }) {
                    Text("move")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = {
                    game = game.spawnShaman(
                        listOf(Team.Forest, Team.Sea).random(),
                        Pos(Random.nextInt(0, 5), Random.nextInt(0, 5))
                    )
                }) {
                    Text("spawn")
                }
            }
        }
    }
}
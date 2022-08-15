@file:OptIn(ExperimentalFoundationApi::class)

package com.example.cairnclone.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cairnclone.R
import com.example.cairnclone.game.board.MoveActionTile
import com.example.cairnclone.game.board.SpawnActionTile
import com.example.cairnclone.game.board.TransformationTile


@Composable
fun TransformationTilePiece(tile: TransformationTile) {
    Box(
        Modifier
            .size(75.dp)
            .background(Color.Black)
            .padding(4.dp)
            .background(Color.Magenta)
    ) {
        val personColors =
            if (tile == TransformationTile.Surrounded)
                listOf(Color.Black, null, null, null, Color.Red, null, null, null, Color.Black)
            else
                listOf(null, null, null, Color.Black, Color.Black, Color.Red, null, null, null)

        LazyVerticalGrid(
            cells = GridCells.Fixed(3),
        ) {
            items(personColors.size) { index ->
                val color = personColors[index]
                Icon(Icons.Default.Person, "", tint = color ?: Color.Transparent)

            }
        }
    }
}

@Composable
fun SpawnTilePiece(tile: SpawnActionTile) {
    Box(
        Modifier
            .size(75.dp)
            .background(Color.Black)
            .padding(4.dp)
            .background(Color.Magenta),
        Alignment.Center
    ) {
        Icon(
            Icons.Filled.PersonAdd,
            "",
            tint =
            if (tile == SpawnActionTile.SpawnWhite) Color.White
            else Color.Black,
            modifier = Modifier.padding(10.dp).fillMaxSize()
        )
    }
}


@Composable
fun MovementTilePiece(tile: MoveActionTile) {
    Box(
        Modifier
            .size(75.dp)
            .background(Color.Black)
            .padding(4.dp)
            .background(Color.Magenta)
    ) {
        ArrowCross(
            if (tile == MoveActionTile.Orthogonally) 0f else 45f
        )
    }
}

@Composable
fun ArrowCross(rotation: Float = 0f) {
    Box(
        Modifier
            .clip(CircleShape)
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = rotation
            },
        contentAlignment = Alignment.Center
    ) {
        DoubleSidedArrow(0f)
        DoubleSidedArrow(90f)
    }
}

@Composable
fun DoubleSidedArrow(rotation: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationZ = rotation
            }
    ) {
        Image(
            painterResource(R.drawable.arrow),
            contentDescription = "",
            modifier = Modifier
                .weight(1f)
                .graphicsLayer {
                    rotationZ = 180f
                    translationX = 4f
                }
        )
        Image(
            painterResource(R.drawable.arrow),
            contentDescription = "",
            modifier = Modifier
                .weight(1f)
                .graphicsLayer {
                    translationX = -4f
                }
        )

    }
}

@Preview
@Composable
fun TilesPreview() {
    Column {
        Row {
            MovementTilePiece(MoveActionTile.Orthogonally)
            Spacer(modifier = Modifier.width(16.dp))
            MovementTilePiece(MoveActionTile.Diagonally)
        }
        Row {
            TransformationTilePiece(TransformationTile.Surrounded)
            Spacer(modifier = Modifier.width(16.dp))
            TransformationTilePiece(TransformationTile.Outnumbered)
        }
        Row {
            SpawnTilePiece(SpawnActionTile.SpawnWhite)
            Spacer(modifier = Modifier.width(16.dp))
            SpawnTilePiece(SpawnActionTile.SpawnBlack)
        }
    }
}

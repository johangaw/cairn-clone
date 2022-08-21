@file:OptIn(ExperimentalFoundationApi::class)

package com.example.cairnclone.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cairnclone.R
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.Shaman
import com.example.cairnclone.game.board.Team


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
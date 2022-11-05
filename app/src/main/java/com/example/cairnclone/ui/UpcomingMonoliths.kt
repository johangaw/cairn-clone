package com.example.cairnclone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cairnclone.game.board.MonolithType

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
                    .clickable(enabled = !disabled, onClick = { onClick(it) })
                    .padding(4.dp),
            ) {
                MonolithPiece(it)
            }
        }
    }
}
package com.example.cairnclone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cairnclone.game.board.Score
import com.example.cairnclone.game.board.Team

@Composable
fun Village(team: Team, active: Boolean, score: Score, onClick: () -> Unit) {
    Box(
        Modifier
            .height(75.dp)
            .fillMaxWidth()
            .background(
                team
                    .let {
                        if (team == Team.Sea) Color.Blue else Color.Green
                    }
                    .let {
                        if (active) it else it.copy(alpha = 0.2f)
                    }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Village (${score.value}/3)", color = Color.White, style = MaterialTheme.typography.h3)
    }
}

@Preview
@Composable
fun VillagePreview() {
    Column {
        Village(team = Team.Forest, active = true, score = Score(0)) {}
        Village(team = Team.Forest, active = false, score = Score(1)) {}
        Village(team = Team.Sea, active = true, score = Score(2)) {}
        Village(team = Team.Sea, active = false, score = Score(3)) {}
    }
}
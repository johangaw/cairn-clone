package com.example.cairnclone.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cairnclone.R
import com.example.cairnclone.game_old.*

@Composable
fun GameBoard(
    game: Game,
    onMoveShaman: (shaman: Shaman, pos: Pos) -> Unit,
    onSpawnShaman: (team: Team, pos: Pos) -> Unit,
    onEndTurn: () -> Unit
) {
    val tileSize = 75
    var selectedShaman by remember { mutableStateOf<Shaman?>(null) }
    Column {
        Box() {
            Column() {
                repeat(game.board.height) { y ->
                    Row() {
                        repeat(game.board.width) { x ->
                            val isWhiteSpawn = game.board.whiteSpawn.values.contains(Pos(x, y))
                            val isBlackSpawn = game.board.blackSpawn.values.contains(Pos(x, y))

                            Box(
                                Modifier
                                    .size(tileSize.dp)
                                    .padding(2.dp)
                                    .background(Color.LightGray)
                                    .padding(2.dp)
                                    .border(
                                        2.dp,
                                        when {
                                            isBlackSpawn -> Color.Black
                                            isWhiteSpawn -> Color.White
                                            else -> Color.LightGray
                                        }
                                    )
                                    .clickable {
                                        if (selectedShaman != null) {
                                            onMoveShaman(selectedShaman!!, Pos(x, y))
                                            selectedShaman = null
                                        } else if (isBlackSpawn || isWhiteSpawn) {
                                            onSpawnShaman(game.activeTeam, Pos(x, y))
                                        }
                                    },

                                )
                        }
                    }
                }
            }

            game.monoliths.forEach { monolith ->
                key(monolith.id) {
                    val offsetX by animateDpAsState((tileSize * monolith.pos.x).dp)
                    val offsetY by animateDpAsState((tileSize * monolith.pos.y).dp)

                    Box(
                        Modifier
                            .size(tileSize.dp)
                            .padding(2.dp)
                            .clickable(enabled = false) {}
                            .offset(offsetX, offsetY),
                        Alignment.Center
                    ) {
                        MonolithPiece(
                            monolith,
                            Modifier.fillMaxSize()
                        )
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
                                .clickable(enabled = shaman.team == game.activeTeam) {
                                    selectedShaman = shaman
                                }
                                .scale(if (selectedShaman == shaman) 1.3f else 1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoPanel(game, onEndTurn)

    }
}

@Composable
fun MonolithPiece(monolith: Monolith, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.Green,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(monolith.power.name, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun InfoPanel(game: Game, onEndTurn: () -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            game.actions.forEach {
                ActionPiece(it)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ScoreTrack(game.activeTeam, onEndTurn)
    }
}

@Composable
fun ScoreTrack(activeTeam: Team, onEndTurn: () -> Unit) {
    Column(Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, if (activeTeam == Team.Forest) Color.Black else Color.Transparent)
                .clickable(onClick = onEndTurn)
        ) {
            Text(Team.Forest.name, style = MaterialTheme.typography.h4)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, if (activeTeam == Team.Sea) Color.Black else Color.Transparent)
                .clickable(onClick = onEndTurn)
        ) {
            Text(Team.Sea.name, style = MaterialTheme.typography.h4)
        }
    }
}

@Composable
fun ActionPiece(it: Action) {
    Box(
        Modifier
            .size(100.dp)
            .background(Color.Blue)
            .padding(8.dp),
        Alignment.Center
    ) {
        Text(it.name, color = Color.White)
    }
}

@Composable
fun ShamanPiece(shaman: Shaman, modifier: Modifier = Modifier) {
    val imageId = when (shaman.team) {
        Team.Forest -> R.drawable.forest_shaman
        Team.Sea -> R.drawable.sea_shaman
    }
    Image(painterResource(id = imageId), contentDescription = "", modifier = modifier)
}

@Composable
fun rememberGameState(initial: Game): Pair<Game, (interaction: Interaction) -> Boolean> {
    var gameState by remember { mutableStateOf<GameState>(SelectAnAction(initial)) }

    return gameState.game to { interaction: Interaction ->
        val newState = gameState.interact(interaction)
        val isNewState = newState == gameState
        gameState = newState
        isNewState
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameBoardPreview() {
    val (game, interact) = rememberGameState(
        Game(
            shamans = setOf(
                Shaman(team = Team.Sea, pos = Pos(2, 0)),
                Shaman(team = Team.Forest, pos = Pos(3, 4))
            ),
            actions = listOf(Action.MoveShamanOrthogonally, Action.SpawnShamanOnWhite),
            monoliths = setOf(Monolith(pos = Pos(2,4), power = MonolithPower.MoveShamanAgain))
        )
    )

    GameBoard(
        game = game,
        onMoveShaman = { shaman, pos -> interact(MoveShaman(shaman, pos)) },
        onSpawnShaman = { team, pos -> interact(SpawnShaman(team, pos)) },
        onEndTurn = { interact(EndTurn()) }
    )

}
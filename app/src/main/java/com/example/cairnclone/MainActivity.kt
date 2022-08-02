package com.example.cairnclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.cairnclone.game_old.*
import com.example.cairnclone.ui.GameBoard
import com.example.cairnclone.ui.rememberGameState
import com.example.cairnclone.ui.theme.CairnCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CairnCloneTheme {
                val (game, interact) = rememberGameState(
                    Game(
                        shamans = setOf(
                            Shaman(team = Team.Sea, pos = Pos(2, 1)),
                            Shaman(team = Team.Forest, pos = Pos(3, 2))
                        ),
                        actions = listOf(Action.MoveShamanOrthogonally, Action.SpawnShamanOnWhite),
                        monoliths = setOf(Monolith(pos = Pos(2,2), power = MonolithPower.MoveShamanAgain))
                    )
                )

                GameBoard(
                    game = game,
                    onMoveShaman = { shaman, pos -> interact(MoveShaman(shaman, pos)) },
                    onSpawnShaman = { team, pos -> interact(SpawnShaman(team, pos)) },
                    onEndTurn = { interact(EndTurn()) }
                )
            }
        }
    }
}
package com.example.cairnclone

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.cairnclone.game.*
import com.example.cairnclone.game.actions.*
import com.example.cairnclone.game.states.WaitForAction
import com.example.cairnclone.ui.CairnBoard
import com.example.cairnclone.ui.theme.CairnCloneTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val LOG_TAG = Game::javaClass.name

    private val boardStateFlow: MutableSharedFlow<BoardState> = MutableSharedFlow(1)
    private val game: Game = Game(
        WaitForAction(
            BoardState(
                Team.Forest,
                Board(),
                SpawnActionTile.SpawnWhite,
                MoveActionTile.Orthogonally,
                TransformationTile.Outnumbered,
                listOf(),
                listOf(
                    Shaman(team = Team.Forest, pos = Pos(0, 0)),
                    Shaman(team = Team.Forest, pos = Pos(1, 1)),
                    Shaman(team = Team.Sea, pos = Pos(2, 1)),
                    Shaman(team = Team.Sea, pos = Pos(4, 4))
                ),
                listOf(),
                listOf(),
                listOf(),
                Scores(Score(0), Score(0))
            )
        ),
        ::publishNewBoardState
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CairnCloneTheme {
                val state by boardStateFlow.collectAsState(initial = game.gameState.boardState)
                CairnBoard(
                    state = state,
                    performMove = { shaman, newPos ->
                        game.perform(
                            MoveShaman(
                                shaman,
                                state.activeTeam,
                                newPos
                            )
                        )
                    },
                    performSpawn = {
                        val inactiveShaman =
                            state.inactiveShamans.find { shaman -> shaman.team == state.activeTeam }
                        if (inactiveShaman != null) game.perform(
                            SpawnShaman(inactiveShaman)
                        ) else false
                    },
                    performEndTurn = {
                        game.perform(EndTurn)
                    },
                    performSelectMonolith = {
                        game.perform(SelectMonolith(it))
                    },
                    performTransformation = { s1, s2, target ->
                        game.perform(
                            TransformShaman(
                                s1,
                                s2,
                                target
                            )
                        )
                    }
                )
            }
        }
    }

    private fun publishNewBoardState(bs: BoardState) {
        lifecycleScope.launch {
            boardStateFlow.emit(bs)
        }
    }
}
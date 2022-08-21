package com.example.cairnclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.cairnclone.game.Game
import com.example.cairnclone.game.MonolithType
import com.example.cairnclone.game.board.BoardState
import com.example.cairnclone.game.board.buildBoard
import com.example.cairnclone.game.states.*
import com.example.cairnclone.game.states.monoliths.ActivatingChaosOfTheGiants
import com.example.cairnclone.game.states.monoliths.MonolithGameState
import com.example.cairnclone.ui.CairnBoard
import com.example.cairnclone.ui.GameStage
import com.example.cairnclone.ui.rememberClickBasedCairnBoardState
import com.example.cairnclone.ui.theme.CairnCloneTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val LOG_TAG = Game::javaClass.name

    private val gameStateFlow: MutableSharedFlow<BoardState> = MutableSharedFlow(1)
    private val gamePhaseFlow: MutableSharedFlow<GameStage> = MutableSharedFlow(1)
    private val game: Game = Game(
        WaitForAction(
            buildBoard {
                emptyBoard()
                positionStartShamans()
                positionStartMonoliths()
            }
        ),
        ::publishNewState
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CairnCloneTheme {
                val state by gameStateFlow.collectAsState(initial = game.gameState.boardState)
                val phase by gamePhaseFlow.collectAsState(initial = GameStage.Action)
                val uiState = rememberClickBasedCairnBoardState(game)
                CairnBoard(
                    state = state,
                    stage = phase,
                    uiState
                )
            }
        }
    }

    private fun publishNewState(gs: GameState) {
        lifecycleScope.launch {
            gameStateFlow.emit(gs.boardState)
            when (gs) {
                is WaitForAction -> gamePhaseFlow.emit(GameStage.Action)
                is WaitForTransformation -> gamePhaseFlow.emit(GameStage.Transformation)
                is WaitForNewMonolith -> gamePhaseFlow.emit(GameStage.SelectMonolith)
                is MonolithGameState -> gamePhaseFlow.emit(GameStage.ActivatingMonolith(gs.monolith))
                is EndingTurn -> gamePhaseFlow.emit(GameStage.End)
                is ActivatingChaosOfTheGiants -> gamePhaseFlow.emit(
                    GameStage.ActivatingMonolith(
                        MonolithType.ChaosOfTheGiants
                    )
                )
                else -> {}
            }
        }
    }
}
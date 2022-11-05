package com.example.cairnclone.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.cairnclone.game.Game
import com.example.cairnclone.game.board.*
import com.example.cairnclone.game.states.WaitForAction
import com.example.cairnclone.ui.draganddrop.DragTarget
import com.example.cairnclone.ui.draganddrop.DropTarget

private val SpawnActionTile.positions: List<Pos> get() = listOf(this.forest, this.sea)

sealed class GameStage {
    object Action : GameStage()
    object SelectMonolith : GameStage()
    object Transformation : GameStage()
    data class ActivatingMonolith(val monolith: MonolithType) : GameStage()
    object End : GameStage()
}

@Composable
fun CairnBoard(
    state: BoardState,
    stage: GameStage,
    uiState: DADBasedCairnBoardState,
) {
    val selectedPositions = uiState.selectedPositions
    val selectedShamans = uiState.selectedShamans
    val selectedMonolith = uiState.selectedMonolith

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DropTarget(dadContext = LocalCairnBoardDADContext.current, onDrop = {
            uiState.handleMoveToVillage(it, Team.Forest, state, stage)
        }) {
            Village(
                Team.Forest,
                state.activeTeam == Team.Forest,
                state.scores.forestTeam,
                selected = it != null
            )
        }

        PositionGrid(
            columns = state.board.width,
            rows = state.board.height,
            modifier = Modifier.zIndex(1f)
        ) {
            val pieceType = when {
                SpawnActionTile.Black.positions.contains(pos) -> BoardPieceType.BlackSpawn
                SpawnActionTile.White.positions.contains(pos) -> BoardPieceType.WhiteSpawn
                else -> BoardPieceType.Normal
            }

            val scope = this
            DropTarget(
                dadContext = LocalCairnBoardDADContext.current,
                onDrop = { uiState.handleMoveToPos(it, pos, state, stage) }

            ) { draggedShaman ->
                val canDrop = draggedShaman?.let { it.pos != pos } ?: false

                BoardPiece(
                    type = pieceType,
                    selected = canDrop || selectedPositions.contains(pos),
                    onClick = { uiState.handleBoardClick(pos, state, stage) },
                    onLongClick = { uiState.handleLongBoardClick(pos, state) }
                ) {
                    state.monolithAt(pos)?.let { monolith ->
                        MonolithPiece(
                            monolithType = monolith.type,
                        )
                    }
                    state.shamanAt(pos)?.let { shaman ->
                        DragTarget(
                            buildDragData = { shaman },
                            dadContext = LocalCairnBoardDADContext.current,
                            modifier = Modifier.zIndex(1f),
                        ) { dragging ->
                            scope.dragging = dragging
                            ShamanPiece(
                                shaman,
                                selected = dragging || selectedShamans.contains(shaman),
                                modifier = Modifier.zIndex(1f)
                            )
                        }
                    }
                }
            }
        }

        DropTarget(dadContext = LocalCairnBoardDADContext.current, onDrop = {
            uiState.handleMoveToVillage(it, Team.Sea, state, stage)
        }) {
            Village(
                Team.Sea,
                state.activeTeam == Team.Sea,
                state.scores.seaTeam,
                selected = it != null
            )
        }

        Divider(thickness = 4.dp, color = Color.Black, modifier = Modifier.padding(8.dp, 8.dp))

        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceAround,
            Alignment.CenterVertically
        ) {
            SpawnTilePiece(state.spawnActionTile)
            MovementTilePiece(state.moveActionTile)
            JumpTilePiece(state.jumpActionTile)
            TransformationTilePiece(state.transformationTile)
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (stage == GameStage.SelectMonolith)
                UpcomingMonoliths(
                    state.upcomingMonoliths,
                    {
                        uiState.handleUpcomingMonolithClick(it)
                    },
                )

            if (stage == GameStage.Transformation)
                TransformButton(
                    onClick = { uiState.handleTransformClick() })

            if (stage == GameStage.Transformation)
                EndRoundButton(
                    onClick = { uiState.handleEndTurnClick() })

            if (stage is GameStage.ActivatingMonolith)
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ActivateMonolithButton(
                        onClick = {
                            uiState.handleActivateMonolith(stage.monolith)
                        },
                        disabled = setOf(
                            MonolithType.CromlechOfTheStars,
                            MonolithType.DeerRock,
                            MonolithType.MenhirOfTheDancers,
                        ).contains(stage.monolith)
                    )

                    Text(stage.monolith.description)
                }
        }

        if (selectedMonolith != null)
            AlertDialog(
                onDismissRequest = { uiState.unselectMonolith() },
                confirmButton = { TextButton({ uiState.unselectMonolith() }) { Text("Ok") } },
                title = { Text(selectedMonolith.type.name) },
                text = { Text(selectedMonolith.type.description) }
            )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CairnBoardPreview() {
    val state = remember {
        buildBoard {
            emptyBoard()
            positionStartShamans()
            positionStartMonoliths()
        }
    }
    val uiState = rememberDADBasedCairnBoardState(Game(WaitForAction(state), {}))
    CairnBoard(
        state,
        GameStage.Action,
        uiState
    )
}
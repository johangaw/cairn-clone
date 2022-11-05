package com.example.cairnclone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.cairnclone.game.board.Pos
import com.example.cairnclone.ui.draganddrop.DragAndDropContext
import com.example.cairnclone.ui.draganddrop.DragTarget

class PositionGridScopeCollection {
    private val scopes: MutableMap<Pos, PositionGridScope> = mutableMapOf()
    fun get(pos: Pos): PositionGridScope = scopes.getOrPut(pos) { PositionGridScope(pos) }

    fun isRowDragging(rowIndex: Int) =
        scopes.values.filter { it.pos.y == rowIndex }.any { it.dragging }
}

class PositionGridScope(val pos: Pos) {
    var dragging by mutableStateOf(false)
}

@Composable
fun PositionGrid(columns: Int, rows: Int, modifier: Modifier = Modifier, content: @Composable PositionGridScope.() -> Unit) {

    val scopes = remember { PositionGridScopeCollection() }

    Column(modifier) {
        repeat(rows) { row ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .zIndex(if (scopes.isRowDragging(row)) 1f else 0f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(columns) { column ->
                    val pos = Pos(column, row)
                    val scope = scopes.get(pos)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .zIndex(if (scope.dragging) 1f else 0f),
                    ) {
                        scope.content()
                    }
                }
            }
        }
    }
}

private val LocalDADContext = compositionLocalOf { DragAndDropContext<Pos>() }

@Composable
@Preview(showSystemUi = true)
fun PositionGridPreview() {
    PositionGrid(columns = 5, rows = 5) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(4.dp)
                .background(Color.Green),
            Alignment.Center
        ) {
            when (pos) {
                Pos(1, 3) -> {
                    DragTarget(
                        buildDragData = { pos },
                        dadContext = LocalDADContext.current
                    ) { dragging ->
                        this@PositionGrid.dragging = dragging
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                    }
                }
                Pos(4, 1) -> {
                    DragTarget(
                        buildDragData = { pos },
                        dadContext = LocalDADContext.current
                    ) { dragging ->
                        this@PositionGrid.dragging = dragging
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.Blue)
                        )
                    }
                }
            }
        }
    }
}
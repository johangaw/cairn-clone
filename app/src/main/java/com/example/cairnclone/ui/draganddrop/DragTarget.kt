package com.example.cairnclone.ui.draganddrop

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt


@Composable
fun <T> DragTarget(
    buildDragData: () -> T,
    dadContext: DragAndDropContext<T>,
    modifier: Modifier = Modifier,
    content: @Composable (dragging: Boolean) -> Unit = {},
) {
    var dragGlobalOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    var dragging by remember { mutableStateOf(false) }

    fun resetCurrentContext() {
        dadContext.dragOffset = Offset.Zero
        dadContext.dragPosition = Offset.Zero
        dadContext.dragData = null
        dragging = false
    }

    Box(
        modifier
            .offset {
                if (dragging)
                    IntOffset(
                        dadContext.dragOffset.x.roundToInt(),
                        dadContext.dragOffset.y.roundToInt()
                    ) else IntOffset.Zero
            }
            .onGloballyPositioned { dragGlobalOffset = it.localToWindow(Offset.Zero) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragging = true
                        dadContext.dragPosition = dragGlobalOffset + it
                        dadContext.dragData = buildDragData()
                    },
                    onDragEnd = {
                        val dropLocation = dadContext.dragPosition + dadContext.dragOffset
                        dadContext.dragData?.let { dropData ->
                            dadContext.dropTargets.values
                                .firstOrNull { it.rect.contains(dropLocation) }
                                ?.cb?.invoke(dropData)
                        }
                        resetCurrentContext()
                    },
                    onDragCancel = { resetCurrentContext() },
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                        dadContext.dragOffset += dragAmount
                    },
                )
            }
    ) {
        content(dragging)
    }
}
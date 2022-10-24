package com.example.cairnclone.ui.draganddrop

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLifecycleOwner
import java.util.*

@Composable
fun <T> DropTarget(
    dadContext: DragAndDropContext<T>,
    onDrop: (data: T) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (hoveredDropData: T?) -> Unit = {},
) {
    var positionInWindow by remember {
        mutableStateOf(Rect(0f, 0f, 0f, 0f))
    }

    val dropTargetId = remember { UUID.randomUUID().toString() }
    DisposableEffect(LocalLifecycleOwner.current) {
        dadContext.dropTargets[dropTargetId] = DropTargetData(positionInWindow, onDrop)
        onDispose {
            dadContext.dropTargets.remove(dropTargetId)
        }
    }

    Box(modifier = modifier.onGloballyPositioned {
        if (dadContext.dropTargets.contains(dropTargetId)) {
            positionInWindow = it.boundsInWindow()
            dadContext.dropTargets[dropTargetId] = DropTargetData(positionInWindow, onDrop)
        }
    }) {
        val hovered = positionInWindow.contains(dadContext.dragPosition + dadContext.dragOffset)
        content(if (hovered) dadContext.dragData else null)
    }
}
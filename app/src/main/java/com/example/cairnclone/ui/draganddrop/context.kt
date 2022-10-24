package com.example.cairnclone.ui.draganddrop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class DropTargetData<T>(val rect: Rect, val cb: (data: T) -> Unit)

class DragAndDropContext<T> {
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var dropTargets = mutableStateMapOf<String, DropTargetData<T>>()
    var dragData by mutableStateOf<T?>(null)
}


val LocalPreviewDADContext = compositionLocalOf { DragAndDropContext<Int>() }

@Composable
@Preview(showSystemUi = true)
fun DragAndDropPreview() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxSize()) {

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) {
                    var text by remember { mutableStateOf("") }
                    DropTarget(
                        LocalPreviewDADContext.current,
                        { text = it.toString() }
                    ) {
                        Box(
                            Modifier
                                .size(100.dp)
                                .background(if (it != null) Color.Red else Color.Green),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text)
                        }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            repeat(5) { data ->
                DragTarget(
                    { data },
                    LocalPreviewDADContext.current,
                ) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = data.toString(), color = Color.White)
                    }
                }
            }
        }
    }
}



package com.example.cairnclone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    disabled: Boolean,
    text: String,
    color: Color,
    contentColor: Color
) {
    Box(
        Modifier
            .size(75.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = if (disabled) 0.1f else 1f))
            .clickable(enabled = !disabled) { onClick() },
        Alignment.Center,
    ) {
        Text(text = text, color = contentColor)
    }
}

@Composable
fun TransformButton(onClick: () -> Unit, disabled: Boolean = false) {
    ActionButton(onClick, disabled, "Transform", Color.Blue, Color.White)
}

@Composable
fun EndRoundButton(onClick: () -> Unit, disabled: Boolean = false) {
    ActionButton(onClick, disabled, "End Turn", Color.Red, Color.White)
}

@Composable
fun ActivateMonolithButton(onClick: () -> Unit, disabled: Boolean = false) {
    val darkGreen = Color(0, 100, 0)
    ActionButton(onClick, disabled, "Activate", darkGreen, Color.White)
}
package com.yossibank.androidapptemplate.core.screen.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.skeleton(): Modifier {
    val progress = rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1500, easing = LinearEasing)),
        label = "skeleton",
    )

    return graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()

            val start = size.width * (progress.value * 2 - 1)

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Black, Color.Black.copy(alpha = 0.6f)),
                    startX = start,
                    endX = start + size.width,
                ),
                blendMode = BlendMode.DstIn,
            )
        }
}

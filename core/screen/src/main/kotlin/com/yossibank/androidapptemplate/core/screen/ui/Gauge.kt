package com.yossibank.androidapptemplate.core.screen.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Gauge(
    fraction: Float?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    label: @Composable () -> Unit,
) {
    val animated by animateFloatAsState(targetValue = fraction ?: 0f, label = "gauge")

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 3.dp,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            if (fraction != null) {
                CircularProgressIndicator(
                    progress = { animated },
                    strokeWidth = 2.dp,
                    trackColor = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(12.dp),
                )
            }

            ProvideTextStyle(
                MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontFeatureSettings = "tnum",
                ),
            ) {
                label()
            }
        }
    }
}

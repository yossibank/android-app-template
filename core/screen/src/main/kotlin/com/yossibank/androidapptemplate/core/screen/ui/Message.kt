package com.yossibank.androidapptemplate.core.screen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yossibank.androidapptemplate.core.screen.R

@Composable
fun Message(
    text: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 1.dp)
                .background(Atelier.palette.ink),
        )

        Text(text = text, style = Atelier.mincho(20.sp, bold = true), textAlign = TextAlign.Center)

        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Atelier.palette.muted,
                textAlign = TextAlign.Center,
            )
        }

        if (onRetry != null) {
            OutlineButton(
                text = stringResource(R.string.screen_retry),
                onClick = onRetry,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

package com.yossibank.androidapptemplate.core.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yossibank.androidapptemplate.core.screen.ui.Message

@Composable
fun <T> Screen(
    phase: FetchPhase<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isEmpty: (T) -> Boolean = { false },
    loading: @Composable () -> Unit = { DefaultLoading() },
    empty: @Composable () -> Unit = {},
    content: @Composable (T) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (phase) {
            FetchPhase.Idle, FetchPhase.Loading -> loading()

            is FetchPhase.Loaded -> if (isEmpty(phase.value)) empty() else content(phase.value)

            is FetchPhase.Failed -> Message(
                text = stringResource(R.string.screen_load_failed),
                description = phase.failure.text(),
                color = MaterialTheme.colorScheme.error,
                onRetry = onRetry.takeIf { phase.failure.canRetry },
            )
        }
    }
}

@Composable
private fun DefaultLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

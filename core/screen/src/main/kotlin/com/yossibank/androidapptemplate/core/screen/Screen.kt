package com.yossibank.androidapptemplate.core.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yossibank.androidapptemplate.core.screen.ui.Message

@Composable
fun <T> LiveScreen(
    viewModel: ScreenViewModel<T>,
    onSessionEnded: () -> Unit = {},
    content: @Composable (phase: FetchPhase<T>, actions: ScreenActions) -> Unit,
) {
    val phase by viewModel.fetchState.phase.collectAsStateWithLifecycle()
    val running by viewModel.fetchState.running.collectAsStateWithLifecycle()
    val sessionEnded by viewModel.fetchState.sessionEnded.collectAsStateWithLifecycle()

    LaunchedEffect(sessionEnded) {
        if (sessionEnded) onSessionEnded()
    }

    LaunchedEffect(viewModel) {
        viewModel.start()
    }

    val actions = remember(viewModel, running) {
        ScreenActions(
            reload = { viewModel.request(FetchOperation.RELOAD) },
            refresh = { viewModel.request(FetchOperation.REFRESH) },
            loadMore = { viewModel.request(FetchOperation.LOAD_MORE) },
            isRefreshing = running == FetchOperation.REFRESH,
            isLoadingMore = running == FetchOperation.LOAD_MORE,
        )
    }

    content(phase, actions)
}

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

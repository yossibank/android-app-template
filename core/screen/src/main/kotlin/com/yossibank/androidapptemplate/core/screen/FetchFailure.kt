package com.yossibank.androidapptemplate.core.screen

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yossibank.shared.core.ApiFailure

data class FetchFailure(
    @param:StringRes val messageRes: Int,
    val formatArgs: List<Any> = emptyList(),
    val canRetry: Boolean = true,
) : Exception() {
    companion object {
        val offline: FetchFailure get() = FetchFailure(R.string.screen_offline)

        val timeout: FetchFailure get() = FetchFailure(R.string.screen_timeout)

        val unreadable: FetchFailure get() = FetchFailure(R.string.screen_unreadable, canRetry = false)

        fun unexpected(canRetry: Boolean): FetchFailure = FetchFailure(R.string.screen_unexpected, canRetry = canRetry)

        fun server(
            statusCode: Int,
            canRetry: Boolean,
        ): FetchFailure = FetchFailure(R.string.screen_server_error, listOf(statusCode), canRetry)
    }
}

fun ApiFailure.toFetchFailure(): FetchFailure = when (this) {
    ApiFailure.Offline -> FetchFailure.offline
    ApiFailure.Timeout -> FetchFailure.timeout
    is ApiFailure.Server -> FetchFailure.server(statusCode, canRetry)
    ApiFailure.Unreadable -> FetchFailure.unreadable
    ApiFailure.Closed -> FetchFailure.unexpected(canRetry = false)
}

@Composable
fun FetchFailure.text(): String = stringResource(messageRes, *formatArgs.toTypedArray())

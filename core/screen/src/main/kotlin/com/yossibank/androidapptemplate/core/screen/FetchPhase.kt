package com.yossibank.androidapptemplate.core.screen

sealed interface FetchPhase<out T> {
    data object Idle : FetchPhase<Nothing>

    data object Loading : FetchPhase<Nothing>

    data class Loaded<T>(
        val value: T,
    ) : FetchPhase<T>

    data class Failed(
        val failure: FetchFailure,
    ) : FetchPhase<Nothing>
}

sealed interface FetchMore<out T> {
    data class More<T>(
        val value: T,
    ) : FetchMore<T>

    data class Last<T>(
        val value: T,
    ) : FetchMore<T>

    data object Unchanged : FetchMore<Nothing>
}

internal enum class FetchOperation {
    RELOAD,
    REFRESH,
    LOAD_MORE,
}

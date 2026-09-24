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
    val value: T

    data class More<T>(
        override val value: T,
    ) : FetchMore<T>

    data class Last<T>(
        override val value: T,
    ) : FetchMore<T>
}

enum class FetchOperation {
    RELOAD,
    REFRESH,
    LOAD_MORE,
}

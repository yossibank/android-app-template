package com.yossibank.androidapptemplate.core.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

abstract class ScreenViewModel<T> : ViewModel() {
    val fetchState = FetchState<T>(viewModelScope)

    abstract suspend fun fetch(): T

    open suspend fun fetchMore(): FetchMore<T>? = null

    fun start() {
        if (fetchState.phase.value !is FetchPhase.Idle) return

        request(FetchOperation.RELOAD)
    }

    fun request(operation: FetchOperation) {
        when (operation) {
            FetchOperation.RELOAD -> fetchState.reload(::fetch)
            FetchOperation.REFRESH -> fetchState.refresh(::fetch)
            FetchOperation.LOAD_MORE -> fetchState.loadMore(::fetchMore)
        }
    }
}

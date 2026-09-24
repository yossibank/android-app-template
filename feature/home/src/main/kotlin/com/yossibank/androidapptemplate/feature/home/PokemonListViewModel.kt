package com.yossibank.androidapptemplate.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yossibank.androidapptemplate.core.screen.LatestResult
import com.yossibank.shared.core.ApiFailure
import com.yossibank.shared.pokemon.PokemonEntry
import com.yossibank.shared.pokemon.PokemonListResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PokemonListViewModel(
    private val paging: PokemonPaging = SharedPokemonPaging(),
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<PokemonListUiState>(PokemonListUiState.Loading)
    val uiState: StateFlow<PokemonListUiState> = mutableUiState.asStateFlow()

    private val latest = LatestResult(viewModelScope, mutableUiState)

    init {
        reload()
    }

    fun reload() {
        latest.restart {
            mutableUiState.value = PokemonListUiState.Loading
            paging.reset()
            nextPage()
        }
    }

    fun refresh() {
        val current = mutableUiState.value

        if (current !is PokemonListUiState.Loaded) {
            reload()
            return
        }

        latest.restart {
            mutableUiState.value = current.copy(isRefreshing = true, notice = null)
            paging.reset()
            nextPage()
        }
    }

    fun loadMore() {
        val current = mutableUiState.value

        if (current !is PokemonListUiState.Loaded || !current.hasMore) return

        latest.startIfIdle {
            mutableUiState.value = current.copy(isLoadingMore = true, notice = null)
            fetched(Notice.Retry.LOAD_MORE) { paging.loadNext() }
        }
    }

    fun retryMissingDetails() {
        val current = mutableUiState.value

        if (current !is PokemonListUiState.Loaded || current.incompleteCount == 0) return

        latest.startIfIdle {
            mutableUiState.value = current.copy(isRepairingDetails = true, notice = null)
            fetched(Notice.Retry.REPAIR) { paging.retryMissingDetails() }
        }
    }

    override fun onCleared() {
        paging.close()
    }

    private suspend fun nextPage(): PokemonListUiState = fetched(Notice.Retry.LOAD_MORE) { paging.loadNext() }

    private suspend fun fetched(
        retry: Notice.Retry,
        fetch: suspend () -> PokemonListResult,
    ): PokemonListUiState = try {
        when (val result = fetch()) {
            is PokemonListResult.Loaded ->
                loaded(
                    result.pokemon,
                    result.hasMore,
                    result.incompleteCount,
                    result.total,
                    result.failure?.let { Notice(it.toErrorMessage(), retry) },
                )

            is PokemonListResult.Failed ->
                PokemonListUiState.Failed(result.failure.toErrorMessage())

            PokemonListResult.Stale ->
                settled(mutableUiState.value)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        PokemonListUiState.Failed(ErrorMessage(R.string.error_unexpected, canRetry = true))
    }

    private fun settled(uiState: PokemonListUiState): PokemonListUiState = if (uiState is PokemonListUiState.Loaded) {
        uiState.copy(isLoadingMore = false, isRepairingDetails = false, isRefreshing = false)
    } else {
        uiState
    }

    private fun loaded(
        pokemon: List<PokemonEntry>,
        hasMore: Boolean,
        incompleteCount: Int,
        total: Int,
        notice: Notice?,
    ): PokemonListUiState = if (pokemon.isEmpty()) {
        PokemonListUiState.Empty
    } else {
        PokemonListUiState.Loaded(
            pokemon = pokemon,
            hasMore = hasMore,
            incompleteCount = incompleteCount,
            total = total,
            notice = notice,
        )
    }
}

private fun ApiFailure.toErrorMessage(): ErrorMessage = when (this) {
    is ApiFailure.Offline -> ErrorMessage(R.string.error_offline, canRetry)
    is ApiFailure.Timeout -> ErrorMessage(R.string.error_timeout, canRetry)
    is ApiFailure.Server -> ErrorMessage(R.string.error_server, canRetry, listOf(statusCode))
    is ApiFailure.Unreadable -> ErrorMessage(R.string.error_unreadable, canRetry)
    is ApiFailure.Closed -> ErrorMessage(R.string.error_unexpected, canRetry)
}

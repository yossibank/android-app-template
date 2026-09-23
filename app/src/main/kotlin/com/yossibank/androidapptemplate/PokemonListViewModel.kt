package com.yossibank.androidapptemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yossibank.androidapptemplate.core.LatestResult
import com.yossibank.shared.PokemonEntry
import com.yossibank.shared.PokemonFailure
import com.yossibank.shared.PokemonListResult
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

    fun loadMore() {
        val current = mutableUiState.value

        if (current !is PokemonListUiState.Loaded || !current.hasMore) return

        latest.startIfIdle {
            mutableUiState.value = current.copy(isLoadingMore = true, notice = null)
            fetched { paging.loadNext() }
        }
    }

    fun retryMissingDetails() {
        val current = mutableUiState.value

        if (current !is PokemonListUiState.Loaded || current.incompleteCount == 0) return

        latest.startIfIdle {
            mutableUiState.value = current.copy(isRepairingDetails = true, notice = null)
            fetched { paging.retryMissingDetails() }
        }
    }

    override fun onCleared() {
        paging.close()
    }

    private suspend fun nextPage(): PokemonListUiState = fetched { paging.loadNext() }

    private suspend fun fetched(fetch: suspend () -> PokemonListResult): PokemonListUiState = try {
        when (val result = fetch()) {
            is PokemonListResult.Loaded ->
                loaded(result.pokemon, result.hasMore, result.incompleteCount, notice = null)

            is PokemonListResult.Degraded ->
                loaded(result.pokemon, result.hasMore, result.incompleteCount, result.failure.toNotice())

            is PokemonListResult.Failed ->
                result.failure.toFailed()

            PokemonListResult.Stale ->
                settled(mutableUiState.value)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        PokemonListUiState.Failed(
            messageRes = R.string.error_unexpected,
            canRetry = true,
        )
    }

    private fun settled(uiState: PokemonListUiState): PokemonListUiState = if (uiState is PokemonListUiState.Loaded) {
        uiState.copy(isLoadingMore = false, isRepairingDetails = false)
    } else {
        uiState
    }

    private fun loaded(
        pokemon: List<PokemonEntry>,
        hasMore: Boolean,
        incompleteCount: Int,
        notice: PokemonListUiState.Notice?,
    ): PokemonListUiState = if (pokemon.isEmpty()) {
        PokemonListUiState.Empty
    } else {
        PokemonListUiState.Loaded(
            pokemon = pokemon,
            hasMore = hasMore,
            incompleteCount = incompleteCount,
            notice = notice,
        )
    }
}

private fun PokemonFailure.toFailed(): PokemonListUiState.Failed = when (this) {
    is PokemonFailure.Offline ->
        PokemonListUiState.Failed(R.string.error_offline, canRetry)

    is PokemonFailure.Timeout ->
        PokemonListUiState.Failed(R.string.error_timeout, canRetry)

    is PokemonFailure.Server ->
        PokemonListUiState.Failed(R.string.error_server, canRetry, listOf(statusCode))

    is PokemonFailure.Unexpected ->
        PokemonListUiState.Failed(R.string.error_unreadable, canRetry)

    is PokemonFailure.Closed ->
        PokemonListUiState.Failed(R.string.error_unexpected, canRetry)
}

private fun PokemonFailure.toNotice(): PokemonListUiState.Notice = toFailed().let {
    PokemonListUiState.Notice(it.messageRes, it.canRetry, it.formatArgs)
}

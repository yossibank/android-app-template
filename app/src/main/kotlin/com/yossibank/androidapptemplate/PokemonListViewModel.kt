package com.yossibank.androidapptemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yossibank.androidapptemplate.core.LatestResult
import com.yossibank.shared.PokemonEntry
import com.yossibank.shared.PokemonListFailure
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
            nextPage()
        }
    }

    override fun onCleared() {
        paging.close()
    }

    private suspend fun nextPage(): PokemonListUiState = try {
        when (val result = paging.loadNext()) {
            is PokemonListResult.Loaded ->
                loaded(result.pokemon, result.hasMore, result.incompleteCount, notice = null)

            is PokemonListResult.Failed ->
                if (result.pokemon.isEmpty()) {
                    result.failure.toFailed()
                } else {
                    loaded(result.pokemon, result.hasMore, result.incompleteCount, result.failure.toNotice())
                }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        PokemonListUiState.Failed(
            messageRes = R.string.error_unexpected,
            canRetry = true,
        )
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

private fun PokemonListFailure.toFailed(): PokemonListUiState.Failed = when (this) {
    is PokemonListFailure.Offline ->
        PokemonListUiState.Failed(R.string.error_offline, canRetry)

    is PokemonListFailure.Server ->
        PokemonListUiState.Failed(R.string.error_server, canRetry, listOf(statusCode))

    is PokemonListFailure.Unexpected ->
        PokemonListUiState.Failed(R.string.error_unreadable, canRetry)
}

private fun PokemonListFailure.toNotice(): PokemonListUiState.Notice = toFailed().let {
    PokemonListUiState.Notice(it.messageRes, it.canRetry, it.formatArgs)
}

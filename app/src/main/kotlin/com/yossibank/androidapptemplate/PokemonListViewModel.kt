package com.yossibank.androidapptemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yossibank.androidapptemplate.core.LatestResult
import com.yossibank.shared.PokemonListFailure
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
            mutableUiState.value = current.copy(isLoadingMore = true)
            nextPage()
        }
    }

    private suspend fun nextPage(): PokemonListUiState = try {
        val result = paging.loadNext()
        val failure = result.failure

        when {
            result.pokemon.isNotEmpty() ->
                PokemonListUiState.Loaded(
                    pokemon = result.pokemon,
                    hasMore = result.hasMore,
                )

            failure != null -> failure.toUiState()

            else -> PokemonListUiState.Empty
        }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        PokemonListUiState.Failed(
            messageRes = R.string.error_unexpected,
            canRetry = true,
        )
    }
}

private fun PokemonListFailure.toUiState(): PokemonListUiState.Failed = when (this) {
    is PokemonListFailure.Offline ->
        PokemonListUiState.Failed(R.string.error_offline, canRetry)

    is PokemonListFailure.Server ->
        PokemonListUiState.Failed(R.string.error_server, canRetry, listOf(statusCode))

    is PokemonListFailure.Unexpected ->
        PokemonListUiState.Failed(R.string.error_unreadable, canRetry)
}

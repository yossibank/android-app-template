package com.yossibank.androidapptemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            mutableUiState.value = current.copy(isLoadingMore = true)

            val next = nextPage()

            // 追加取得が失敗しても、読み込めている分は残す。
            if (next is PokemonListUiState.Loaded) next else current
        }
    }

    private suspend fun nextPage(): PokemonListUiState = try {
        when (val result = paging.loadNext()) {
            is PokemonListResult.Loaded ->
                if (result.pokemon.isEmpty()) {
                    PokemonListUiState.Empty
                } else {
                    PokemonListUiState.Loaded(
                        pokemon = result.pokemon,
                        hasMore = result.hasMore,
                    )
                }

            is PokemonListResult.Failed -> result.toUiState()
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        PokemonListUiState.Failed(
            message = "データを読み取れませんでした",
            canRetry = false,
        )
    }
}

private fun PokemonListResult.Failed.toUiState(): PokemonListUiState.Failed = when (this) {
    is PokemonListResult.Failed.Offline ->
        PokemonListUiState.Failed(
            message = "接続を確認してください",
            canRetry = true,
        )

    is PokemonListResult.Failed.Server ->
        PokemonListUiState.Failed(
            message = "サーバーが応答しませんでした（$statusCode）",
            canRetry = true,
        )

    is PokemonListResult.Failed.Unexpected ->
        PokemonListUiState.Failed(
            message = "データを読み取れませんでした",
            canRetry = false,
        )
}

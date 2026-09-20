package com.yossibank.androidapptemplate

import androidx.annotation.StringRes
import com.yossibank.shared.PokemonEntry

sealed interface PokemonListUiState {
    data object Loading : PokemonListUiState

    data object Empty : PokemonListUiState

    data class Loaded(
        val pokemon: List<PokemonEntry>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : PokemonListUiState

    /**
     * 文言そのものではなく資源 id を持つ。解決は Screen 側で行うので、
     * ViewModel が Context を抱えずに済む。
     */
    data class Failed(
        @StringRes val messageRes: Int,
        val canRetry: Boolean,
        val formatArgs: List<Any> = emptyList(),
    ) : PokemonListUiState
}

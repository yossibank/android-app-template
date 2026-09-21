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

    data class Failed(
        @StringRes val messageRes: Int,
        val canRetry: Boolean,
        val formatArgs: List<Any> = emptyList(),
    ) : PokemonListUiState
}

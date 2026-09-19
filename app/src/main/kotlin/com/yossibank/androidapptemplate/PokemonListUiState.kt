package com.yossibank.androidapptemplate

import com.yossibank.shared.generated.model.PokemonSummary

sealed interface PokemonListUiState {
    data object Loading : PokemonListUiState

    data object Empty : PokemonListUiState

    data class Loaded(
        val pokemon: List<PokemonSummary>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : PokemonListUiState

    data class Failed(
        val message: String,
        val canRetry: Boolean,
    ) : PokemonListUiState
}

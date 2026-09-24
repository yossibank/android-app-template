package com.yossibank.androidapptemplate

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yossibank.shared.PokemonEntry

sealed interface PokemonListUiState {
    data object Loading : PokemonListUiState

    data object Empty : PokemonListUiState

    data class Loaded(
        val pokemon: List<PokemonEntry>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
        val isRepairingDetails: Boolean = false,
        val isRefreshing: Boolean = false,
        val incompleteCount: Int = 0,
        val total: Int = 0,
        val notice: ErrorMessage? = null,
    ) : PokemonListUiState

    data class Failed(
        val error: ErrorMessage,
    ) : PokemonListUiState
}

data class ErrorMessage(
    @StringRes val messageRes: Int,
    val canRetry: Boolean,
    val formatArgs: List<Any> = emptyList(),
) {
    val text: String
        @Composable get() = stringResource(messageRes, *formatArgs.toTypedArray())
}

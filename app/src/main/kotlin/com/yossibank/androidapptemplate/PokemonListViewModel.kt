package com.yossibank.androidapptemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yossibank.shared.PokemonApi
import com.yossibank.shared.PokemonListResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonListViewModel(
    private val fetchPage: suspend () -> PokemonListResult = { PokemonApi().fetchPage() },
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<PokemonListUiState>(PokemonListUiState.Loading)
    val uiState: StateFlow<PokemonListUiState> = mutableUiState.asStateFlow()

    private var loading: Job? = null

    init {
        reload()
    }

    fun reload() {
        loading?.cancel()
        loading = viewModelScope.launch {
            mutableUiState.value = PokemonListUiState.Loading

            val next = try {
                when (val result = fetchPage()) {
                    is PokemonListResult.Loaded ->
                        if (result.pokemon.isEmpty()) {
                            PokemonListUiState.Empty
                        } else {
                            PokemonListUiState.Loaded(result.pokemon)
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

            ensureActive()
            mutableUiState.value = next
        }
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

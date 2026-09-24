package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.FetchMore
import com.yossibank.androidapptemplate.core.screen.ScreenViewModel
import com.yossibank.androidapptemplate.core.screen.toFetchFailure
import com.yossibank.shared.pokemon.PokemonEntry
import com.yossibank.shared.pokemon.PokemonListResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeViewState(
    val total: Int = 0,
    val notice: FetchFailure? = null,
)

class HomeViewModel(
    private val listing: PokemonListing = PokemonPagerListing(),
) : ScreenViewModel<List<PokemonEntry>>() {
    private val mutableViewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = mutableViewState.asStateFlow()

    override suspend fun fetch(): List<PokemonEntry> {
        mutableViewState.update { it.copy(notice = null) }

        val result = listing.reload()

        if (result is PokemonListResult.Failed) {
            throw result.failure.toFetchFailure()
        }

        return loaded(result)?.pokemon.orEmpty()
    }

    override suspend fun fetchMore(): FetchMore<List<PokemonEntry>>? {
        mutableViewState.update { it.copy(notice = null) }

        return loaded(listing.loadNext())?.let { page ->
            if (page.hasMore) FetchMore.More(page.pokemon) else FetchMore.Last(page.pokemon)
        }
    }

    override fun onCleared() {
        listing.close()
    }

    private fun loaded(result: PokemonListResult): PokemonListResult.Loaded? = when (result) {
        is PokemonListResult.Loaded -> {
            mutableViewState.update { it.copy(total = result.total, notice = result.failure?.toFetchFailure()) }
            result
        }

        is PokemonListResult.Failed -> {
            mutableViewState.update { it.copy(notice = result.failure.toFetchFailure()) }
            null
        }

        PokemonListResult.Stale -> null
    }
}

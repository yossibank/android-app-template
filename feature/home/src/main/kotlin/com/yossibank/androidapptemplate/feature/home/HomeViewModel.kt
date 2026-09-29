package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.FetchMore
import com.yossibank.androidapptemplate.core.screen.ScreenViewModel
import com.yossibank.androidapptemplate.core.screen.toFetchFailure
import com.yossibank.shared.pokemon.PokemonListResult

class HomeViewModel(
    private val listing: PokemonListing = PokemonPagerListing(),
) : ScreenViewModel<PokemonList>() {
    override suspend fun fetch(): PokemonList = when (val result = listing.reload()) {
        is PokemonListResult.Loaded -> PokemonList(result.pokemon, result.total)

        is PokemonListResult.Degraded -> PokemonList(result.pokemon, result.total, result.failure.toFetchFailure())

        is PokemonListResult.Failed -> throw result.failure.toFetchFailure()

        PokemonListResult.Stale -> PokemonList(pokemon = emptyList(), total = 0)
    }

    override suspend fun fetchMore(current: PokemonList): FetchMore<PokemonList> = when (val result = listing.loadNext()) {
        is PokemonListResult.Loaded -> page(PokemonList(result.pokemon, result.total), result.hasMore)

        is PokemonListResult.Degraded -> page(
            PokemonList(result.pokemon, result.total, result.failure.toFetchFailure()),
            result.hasMore,
        )

        is PokemonListResult.Failed -> FetchMore.More(current.copy(notice = result.failure.toFetchFailure()))

        PokemonListResult.Stale -> FetchMore.Unchanged
    }

    override fun onCleared() {
        listing.close()
    }

    private fun page(
        list: PokemonList,
        hasMore: Boolean,
    ): FetchMore<PokemonList> = if (hasMore) FetchMore.More(list) else FetchMore.Last(list)
}

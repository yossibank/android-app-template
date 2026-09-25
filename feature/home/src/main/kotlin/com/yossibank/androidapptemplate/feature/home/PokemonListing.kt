package com.yossibank.androidapptemplate.feature.home

import com.yossibank.shared.pokemon.PokemonListResult
import com.yossibank.shared.pokemon.PokemonPager

interface PokemonListing {
    suspend fun reload(): PokemonListResult

    suspend fun loadNext(): PokemonListResult

    fun close()
}

class PokemonPagerListing(
    private val pager: PokemonPager = PokemonPager(),
) : PokemonListing {
    override suspend fun reload(): PokemonListResult {
        pager.reset()
        return pager.loadNext()
    }

    override suspend fun loadNext(): PokemonListResult = pager.loadNext()

    override fun close() = pager.close()
}

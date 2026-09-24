package com.yossibank.androidapptemplate.feature.home

import com.yossibank.shared.pokemon.PokemonListResult
import com.yossibank.shared.pokemon.PokemonPager

interface PokemonPaging {
    suspend fun loadNext(): PokemonListResult

    suspend fun retryMissingDetails(): PokemonListResult

    suspend fun reset()

    fun close()
}

class SharedPokemonPaging(
    private val pager: PokemonPager = PokemonPager(),
) : PokemonPaging {
    override suspend fun loadNext(): PokemonListResult = pager.loadNext()

    override suspend fun retryMissingDetails(): PokemonListResult = pager.retryMissingDetails()

    override suspend fun reset() = pager.reset()

    override fun close() = pager.close()
}

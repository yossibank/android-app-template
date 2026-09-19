package com.yossibank.androidapptemplate

import com.yossibank.shared.PokemonListResult
import com.yossibank.shared.PokemonPager

interface PokemonPaging {
    suspend fun loadNext(): PokemonListResult

    suspend fun reset()
}

class SharedPokemonPaging(
    private val pager: PokemonPager = PokemonPager(),
) : PokemonPaging {
    override suspend fun loadNext(): PokemonListResult = pager.loadNext()

    override suspend fun reset() = pager.reset()
}

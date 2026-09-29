package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.shared.pokemon.PokemonEntry

data class PokemonList(
    val pokemon: List<PokemonEntry>,
    val total: Int,
    val notice: FetchFailure? = null,
)

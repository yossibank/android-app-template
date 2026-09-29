package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.standardContains
import com.yossibank.shared.pokemon.PokemonEntry

fun List<PokemonEntry>.filtered(query: String): List<PokemonEntry> {
    val trimmed = query.trim()

    if (trimmed.isEmpty()) return this

    return filter { it.displayName.standardContains(trimmed) }
}

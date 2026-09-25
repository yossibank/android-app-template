package com.yossibank.androidapptemplate.feature.home

import androidx.annotation.StringRes
import com.yossibank.androidapptemplate.core.screen.standardContains
import com.yossibank.shared.pokemon.PokemonEntry

enum class PokemonSort(
    @param:StringRes val labelRes: Int,
    val comparator: Comparator<PokemonEntry>,
) {
    NUMBER(R.string.home_sort_number, compareBy { it.id }),
    NAME(R.string.home_sort_name, compareBy { it.name }),
}

fun List<PokemonEntry>.filtered(
    query: String,
    sort: PokemonSort,
): List<PokemonEntry> = filter { it.name.standardContains(query) }.sortedWith(sort.comparator)

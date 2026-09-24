package com.yossibank.androidapptemplate.feature.home

import androidx.annotation.StringRes
import com.yossibank.androidapptemplate.core.screen.standardContains
import com.yossibank.shared.pokemon.PokemonEntry
import com.yossibank.shared.pokemon.PokemonEntryDetail
import com.yossibank.shared.pokemon.PokemonTypeKind

enum class PokemonSort(
    @param:StringRes val labelRes: Int,
    val comparator: Comparator<PokemonEntry>,
) {
    NUMBER(R.string.pokemon_list_sort_number, compareBy { it.id }),
    TOTAL(
        R.string.pokemon_list_sort_total,
        compareByDescending { it.loadedDetail?.totalBaseStat ?: -1 },
    ),
    NAME(R.string.pokemon_list_sort_name, compareBy { it.name }),
}

val PokemonEntry.loadedDetail: PokemonEntryDetail.Loaded?
    get() = detail as? PokemonEntryDetail.Loaded

fun List<PokemonEntry>.availableTypes(): List<PokemonTypeKind> = asSequence()
    .mapNotNull { it.loadedDetail }
    .flatMap { it.types }
    .distinct()
    .toList()

fun List<PokemonEntry>.filtered(
    query: String,
    type: PokemonTypeKind?,
    sort: PokemonSort,
): List<PokemonEntry> = filter { it.name.standardContains(query) && it.matches(type) }.sortedWith(sort.comparator)

fun PokemonEntry.matches(type: PokemonTypeKind?): Boolean {
    if (type == null) return true

    return loadedDetail?.types?.contains(type) == true
}

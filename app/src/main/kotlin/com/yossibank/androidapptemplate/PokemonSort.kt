package com.yossibank.androidapptemplate

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.yossibank.shared.PokemonEntry
import com.yossibank.shared.PokemonEntryDetail
import com.yossibank.shared.PokemonTypeKind

enum class PokemonSort(
    @param:StringRes val labelRes: Int,
    val comparator: Comparator<PokemonEntry>,
) {
    NUMBER(R.string.pokemon_list_sort_number, compareBy { it.id }),
    TOTAL(
        R.string.pokemon_list_sort_total,
        compareByDescending { (it.detail as? PokemonEntryDetail.Loaded)?.totalBaseStat ?: -1 },
    ),
    NAME(R.string.pokemon_list_sort_name, compareBy { it.name }),
}

fun List<PokemonEntry>.availableTypes(): List<PokemonTypeKind> = asSequence()
    .mapNotNull { it.detail as? PokemonEntryDetail.Loaded }
    .flatMap { it.types }
    .distinct()
    .sortedBy { it.ordinal }
    .toList()

fun PokemonEntry.matches(type: PokemonTypeKind?): Boolean {
    if (type == null) return true

    return (detail as? PokemonEntryDetail.Loaded)?.types?.contains(type) == true
}

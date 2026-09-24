package com.yossibank.androidapptemplate.feature.home

import com.yossibank.shared.core.ApiFailure
import com.yossibank.shared.pokemon.PokemonEntry
import com.yossibank.shared.pokemon.PokemonEntryDetail
import com.yossibank.shared.pokemon.PokemonTypeKind
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonSortTest {
    @Test
    fun `出てきた型を重複なく、出てきた順に集める`() {
        val pokemon = listOf(
            entry(1, PokemonTypeKind.GRASS, PokemonTypeKind.POISON),
            entry(2, PokemonTypeKind.GRASS, PokemonTypeKind.POISON),
            entry(4, PokemonTypeKind.FIRE),
        )

        assertEquals(
            listOf(PokemonTypeKind.GRASS, PokemonTypeKind.POISON, PokemonTypeKind.FIRE),
            pokemon.availableTypes(),
        )
    }

    @Test
    fun `詳細を取れていない行の型は集めない`() {
        val pokemon = listOf(
            PokemonEntry(132, "ditto", PokemonEntryDetail.Missing(ApiFailure.Offline)),
        )

        assertEquals(emptyList<PokemonTypeKind>(), pokemon.availableTypes())
    }

    private fun entry(
        id: Int,
        vararg types: PokemonTypeKind,
    ) = PokemonEntry(
        id = id,
        name = "p$id",
        detail = PokemonEntryDetail.Loaded(
            imageUrl = null,
            types = types.toList(),
            baseStats = emptyList(),
        ),
    )
}

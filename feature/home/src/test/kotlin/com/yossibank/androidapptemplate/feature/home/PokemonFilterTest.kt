package com.yossibank.androidapptemplate.feature.home

import com.yossibank.shared.pokemon.PokemonEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonFilterTest {
    private val pokemon = listOf(entry(1, "bulbasaur"), entry(4, "charmander"), entry(122, "mr-mime"))

    @Test
    fun `名前で絞り込む`() {
        assertEquals(listOf("charmander"), pokemon.filtered("char").map { it.name })
    }

    @Test
    fun `名前の絞り込みは大文字小文字を区別しない`() {
        assertEquals(listOf("bulbasaur"), pokemon.filtered("BULBA").map { it.name })
    }

    @Test
    fun `前後の空白は無視する`() {
        assertEquals(listOf("charmander"), pokemon.filtered("  char ").map { it.name })
    }

    @Test
    fun `表示名で絞り込む`() {
        assertEquals(listOf("mr-mime"), pokemon.filtered("Mr-Mime").map { it.name })
    }

    @Test
    fun `絞り込んでいなければすべて残る`() {
        assertEquals(pokemon, pokemon.filtered(""))
        assertEquals(pokemon, pokemon.filtered("   "))
    }

    private fun entry(
        id: Int,
        name: String,
    ) = PokemonEntry(id = id, name = name, imageUrl = "")
}

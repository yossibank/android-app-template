package com.yossibank.androidapptemplate.feature.home

import com.yossibank.shared.pokemon.PokemonEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonSortTest {
    @Test
    fun `名前で絞り込む`() {
        val pokemon = listOf(entry(1, "bulbasaur"), entry(4, "charmander"))

        assertEquals(listOf("charmander"), pokemon.filtered("char", PokemonSort.NUMBER).map { it.name })
    }

    @Test
    fun `名前の絞り込みは大文字小文字を区別しない`() {
        assertEquals(1, listOf(entry(1, "bulbasaur")).filtered("BULBA", PokemonSort.NUMBER).size)
    }

    @Test
    fun `絞り込んでいなければすべて残る`() {
        val pokemon = listOf(entry(1, "bulbasaur"), entry(4, "charmander"))

        assertEquals(2, pokemon.filtered("", PokemonSort.NUMBER).size)
    }

    @Test
    fun `番号の小さい順に並べる`() {
        val pokemon = listOf(entry(25, "pikachu"), entry(1, "bulbasaur"))

        assertEquals(listOf(1, 25), pokemon.filtered("", PokemonSort.NUMBER).map { it.id })
    }

    @Test
    fun `名前順に並べる`() {
        val pokemon = listOf(entry(7, "squirtle"), entry(1, "bulbasaur"))

        assertEquals(listOf("bulbasaur", "squirtle"), pokemon.filtered("", PokemonSort.NAME).map { it.name })
    }

    private fun entry(
        id: Int,
        name: String,
    ) = PokemonEntry(id = id, name = name, imageUrl = "")
}

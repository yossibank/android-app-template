package com.yossibank.androidapptemplate.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class TextMatchingTest {
    @Test
    fun `大文字小文字を無視して一致する`() {
        assertTrue("Pikachu".standardContains("pika", Locale.ROOT))
        assertTrue("pikachu".standardContains("PIKA", Locale.ROOT))
    }

    @Test
    fun `発音記号を無視して一致する`() {
        assertTrue("Pokémon".standardContains("pokemon", Locale.ROOT))
        assertTrue("pokemon".standardContains("Pokémon", Locale.ROOT))
    }

    @Test
    fun `合成済みと結合文字で書いた同じ語が一致する`() {
        assertTrue("é".standardContains("é", Locale.ROOT))
    }

    @Test
    fun `空の語はすべてに一致する`() {
        assertTrue("pikachu".standardContains("", Locale.ROOT))
    }

    @Test
    fun `含まれない語には一致しない`() {
        assertFalse("pikachu".standardContains("charizard", Locale.ROOT))
    }

    @Test
    fun `トルコ語ロケールでも I の畳み込みで取りこぼさない`() {
        assertTrue("PIKACHU".standardContains("PIKA", Locale.forLanguageTag("tr")))
    }
}

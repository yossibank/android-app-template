package com.yossibank.androidapptemplate.feature.home

import androidx.compose.ui.graphics.Color
import com.yossibank.shared.product.CatalogEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class CatalogTest {
    @Test
    fun products_are_split_into_chapters_by_page_size() {
        val chapters = list(count = 45, total = 194).chapters

        assertEquals(listOf("I", "II", "III"), chapters.map { it.numeral })
        assertEquals(listOf(1, 21, 41), chapters.map { it.first })
        assertEquals(listOf(20, 40, 60), chapters.map { it.last })
    }

    @Test
    fun the_last_chapter_ends_at_the_total() {
        val last = list(count = 194, total = 194).chapters.last()

        assertEquals(181, last.first)
        assertEquals(194, last.last)
    }

    @Test
    fun chapters_repeat_one_lead_and_a_pair() {
        val spreads = list(count = 20, total = 194).chapters.first().spreads

        assertEquals(listOf(1, 4, 7, 10, 13, 16, 19), spreads.map { it.lead.id })
        assertEquals(listOf(2, 2, 2, 2, 2, 2, 1), spreads.map { it.pair.size })
    }

    @Test
    fun the_next_chapter_is_known_only_after_a_full_chapter() {
        val next = list(count = 40, total = 194).nextChapter

        assertEquals(2, next?.index)
        assertEquals(41, next?.first)
        assertEquals(60, next?.last)
        assertNull(list(count = 45, total = 194).nextChapter)
    }

    @Test
    fun numerals_are_roman() {
        assertEquals(listOf("I", "IV", "IX", "X", "XIV", "XL"), listOf(1, 4, 9, 10, 14, 40).map(Chapter::numeral))
    }

    @Test
    fun ids_are_padded_to_three_digits() {
        assertEquals("006", entry(6).paddedId)
        assertEquals("194", entry(194).paddedId)
    }

    @Test
    fun prices_are_shown_in_dollars() {
        assertEquals("$49.99", entry(6, price = 49.99).priceText(Locale.JAPAN))
    }

    @Test
    fun the_matching_part_of_the_title_is_marked() {
        val marked = entry(4, title = "Red Lipstick").highlightedTitle("red", Color.Yellow, Locale.ROOT)

        assertEquals(1, marked.spanStyles.size)
        assertEquals(0, marked.spanStyles.single().start)
        assertEquals(3, marked.spanStyles.single().end)
        assertTrue(entry(4, title = "Red Lipstick").highlightedTitle("", Color.Yellow).spanStyles.isEmpty())
    }

    private fun list(
        count: Int,
        total: Int,
    ) = ProductList(products = (1..count).map { entry(it) }, total = total, pageSize = 20)

    private fun entry(
        id: Int,
        title: String = "p$id",
        price: Double = 1.0,
    ) = CatalogEntry(id = id, title = title, thumbnailUrl = "", brand = null, price = price)
}

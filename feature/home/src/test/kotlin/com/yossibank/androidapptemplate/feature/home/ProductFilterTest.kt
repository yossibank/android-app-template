package com.yossibank.androidapptemplate.feature.home

import com.yossibank.shared.product.ProductEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductFilterTest {
    private val products = listOf(entry(1, "Red Lipstick"), entry(2, "Powder Canister"), entry(3, "Crème Brûlée"))

    @Test
    fun `商品名で絞り込む`() {
        assertEquals(listOf("Powder Canister"), products.filtered("powder").map { it.title })
    }

    @Test
    fun `商品名の絞り込みは大文字小文字を区別しない`() {
        assertEquals(listOf("Red Lipstick"), products.filtered("LIP").map { it.title })
    }

    @Test
    fun `前後の空白は無視する`() {
        assertEquals(listOf("Red Lipstick"), products.filtered("  red ").map { it.title })
    }

    @Test
    fun `アクセント記号の有無を区別しない`() {
        assertEquals(listOf("Crème Brûlée"), products.filtered("creme brulee").map { it.title })
    }

    @Test
    fun `絞り込んでいなければすべて残る`() {
        assertEquals(products, products.filtered(""))
        assertEquals(products, products.filtered("   "))
    }

    private fun entry(
        id: Int,
        title: String,
    ) = ProductEntry(id = id, title = title, thumbnailUrl = "")
}

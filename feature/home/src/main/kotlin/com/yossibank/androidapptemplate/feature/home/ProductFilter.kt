package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.standardContains
import com.yossibank.shared.product.ProductEntry

fun List<ProductEntry>.filtered(query: String): List<ProductEntry> {
    val trimmed = query.trim()

    if (trimmed.isEmpty()) return this

    return filter { it.title.standardContains(trimmed) }
}

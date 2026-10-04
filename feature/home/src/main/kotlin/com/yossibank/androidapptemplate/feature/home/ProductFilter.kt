package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.standardContains
import com.yossibank.shared.product.CatalogEntry

fun List<CatalogEntry>.filtered(query: String): List<CatalogEntry> {
    val trimmed = query.trim()

    if (trimmed.isEmpty()) return this

    return filter { it.title.standardContains(trimmed) }
}

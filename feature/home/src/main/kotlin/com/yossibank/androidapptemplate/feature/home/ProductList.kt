package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.shared.product.CatalogEntry

data class ProductList(
    val products: List<CatalogEntry>,
    val total: Int,
    val pageSize: Int,
    val notice: FetchFailure? = null,
)

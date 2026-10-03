package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.shared.product.ProductEntry

data class ProductList(
    val products: List<ProductEntry>,
    val total: Int,
    val notice: FetchFailure? = null,
)

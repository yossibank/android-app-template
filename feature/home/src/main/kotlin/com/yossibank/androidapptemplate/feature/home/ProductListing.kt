package com.yossibank.androidapptemplate.feature.home

import com.yossibank.shared.product.ProductListResult
import com.yossibank.shared.product.ProductPager

interface ProductListing {
    suspend fun reload(): ProductListResult

    suspend fun loadNext(): ProductListResult
}

class ProductPagerListing(
    private val pager: ProductPager = ProductPager(),
) : ProductListing {
    override suspend fun reload(): ProductListResult = pager.reload()

    override suspend fun loadNext(): ProductListResult = pager.loadNext()
}

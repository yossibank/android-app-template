package com.yossibank.androidapptemplate.feature.home

import com.yossibank.shared.product.CatalogPager
import com.yossibank.shared.product.CatalogResult

interface ProductListing {
    val pageSize: Int

    suspend fun reload(): CatalogResult

    suspend fun loadNext(): CatalogResult
}

class ProductPagerListing(
    private val pager: CatalogPager = CatalogPager(),
) : ProductListing {
    override val pageSize: Int
        get() = pager.pageSize

    override suspend fun reload(): CatalogResult = pager.reload()

    override suspend fun loadNext(): CatalogResult = pager.loadNext()
}

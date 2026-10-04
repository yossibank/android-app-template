package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.FetchMore
import com.yossibank.androidapptemplate.core.screen.ScreenViewModel
import com.yossibank.androidapptemplate.core.screen.toFetchFailure
import com.yossibank.shared.core.ApiFailure
import com.yossibank.shared.product.CatalogResult

class HomeViewModel(
    private val listing: ProductListing = ProductPagerListing(),
) : ScreenViewModel<ProductList>() {
    override suspend fun fetch(): ProductList = when (val result = listing.reload()) {
        is CatalogResult.Loaded -> ProductList(result.entries, result.total, listing.pageSize)

        is CatalogResult.Degraded -> ProductList(result.entries, result.total, listing.pageSize, result.failure.notice())

        is CatalogResult.Failed -> throw result.failure.toFetchFailure()

        CatalogResult.Stale -> ProductList(products = emptyList(), total = 0, pageSize = listing.pageSize)
    }

    override suspend fun fetchMore(current: ProductList): FetchMore<ProductList> = when (val result = listing.loadNext()) {
        is CatalogResult.Loaded -> page(ProductList(result.entries, result.total, listing.pageSize), result.hasMore)

        is CatalogResult.Degraded -> page(
            ProductList(result.entries, result.total, listing.pageSize, result.failure.notice()),
            result.hasMore,
        )

        is CatalogResult.Failed -> FetchMore.More(current.copy(notice = result.failure.notice()))

        CatalogResult.Stale -> FetchMore.Unchanged
    }

    private fun page(
        list: ProductList,
        hasMore: Boolean,
    ): FetchMore<ProductList> = if (hasMore) FetchMore.More(list) else FetchMore.Last(list)

    private fun ApiFailure.notice(): FetchFailure {
        val failure = toFetchFailure()

        if (failure.endsSession) throw failure

        return failure
    }
}

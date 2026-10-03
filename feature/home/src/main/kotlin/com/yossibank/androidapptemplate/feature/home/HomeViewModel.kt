package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.FetchMore
import com.yossibank.androidapptemplate.core.screen.ScreenViewModel
import com.yossibank.androidapptemplate.core.screen.toFetchFailure
import com.yossibank.shared.core.ApiFailure
import com.yossibank.shared.product.ProductListResult

class HomeViewModel(
    private val listing: ProductListing = ProductPagerListing(),
) : ScreenViewModel<ProductList>() {
    override suspend fun fetch(): ProductList = when (val result = listing.reload()) {
        is ProductListResult.Loaded -> ProductList(result.products, result.total)

        is ProductListResult.Degraded -> ProductList(result.products, result.total, result.failure.notice())

        is ProductListResult.Failed -> throw result.failure.toFetchFailure()

        ProductListResult.Stale -> ProductList(products = emptyList(), total = 0)
    }

    override suspend fun fetchMore(current: ProductList): FetchMore<ProductList> = when (val result = listing.loadNext()) {
        is ProductListResult.Loaded -> page(ProductList(result.products, result.total), result.hasMore)

        is ProductListResult.Degraded -> page(
            ProductList(result.products, result.total, result.failure.notice()),
            result.hasMore,
        )

        is ProductListResult.Failed -> FetchMore.More(current.copy(notice = result.failure.notice()))

        ProductListResult.Stale -> FetchMore.Unchanged
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

package com.yossibank.androidapptemplate.feature.home

import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.FetchMore
import com.yossibank.androidapptemplate.core.screen.FetchPhase
import com.yossibank.shared.core.ApiFailure
import com.yossibank.shared.product.ProductEntry
import com.yossibank.shared.product.ProductListResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

private class StubListing(
    private vararg val pages: ProductListResult,
) : ProductListing {
    private var index = 0

    override suspend fun reload(): ProductListResult {
        index = 0
        return next()
    }

    override suspend fun loadNext(): ProductListResult = next()

    private fun next(): ProductListResult = pages[minOf(index++, pages.size - 1)]
}

private fun entries(titles: Array<out String>) = titles.mapIndexed { index, title ->
    ProductEntry(id = index + 1, title = title, thumbnailUrl = "https://img.example/${index + 1}.webp")
}

private fun loaded(
    vararg titles: String,
    hasMore: Boolean = false,
) = ProductListResult.Loaded(products = entries(titles), hasMore = hasMore, total = 194)

private fun degraded(
    failure: ApiFailure,
    vararg titles: String,
) = ProductListResult.Degraded(products = entries(titles), hasMore = true, total = 194, failure = failure)

private val FetchMore<ProductList>.list: ProductList?
    get() = when (this) {
        is FetchMore.More -> value
        is FetchMore.Last -> value
        FetchMore.Unchanged -> null
    }

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun model(vararg pages: ProductListResult) = HomeViewModel(StubListing(*pages))

    @Test
    fun `取得に成功したら一覧になる`() = runTest(dispatcher) {
        val list = model(loaded("Red Lipstick")).fetch()

        assertEquals(listOf("Red Lipstick"), list.products.map { it.title })
        assertEquals(194, list.total)
        assertNull(list.notice)
    }

    @Test
    fun `続きを読むと一覧が伸びる`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), loaded("a", "b"))

        val more = model.fetchMore(model.fetch())

        assertEquals(listOf("a", "b"), more.list?.products?.map { it.title })
    }

    @Test
    fun `最後のページは終端として返す`() = runTest(dispatcher) {
        val model = model(loaded("a"))

        assertTrue("終端になっていない", model.fetchMore(model.fetch()) is FetchMore.Last)
    }

    @Test
    fun `追加取得が一部失敗したら知らせを立て、続きがあることは残す`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), degraded(ApiFailure.Offline, "a"))

        val more = model.fetchMore(model.fetch())

        assertEquals("追加取得の失敗が握り潰されている", FetchFailure.offline, more.list?.notice)
        assertTrue("失敗しただけで続きが無いことにされている", more is FetchMore.More)
    }

    @Test
    fun `最初の取得の失敗はそのまま失敗として投げる`() = runTest(dispatcher) {
        val model = model(ProductListResult.Failed(ApiFailure.Timeout))

        try {
            model.fetch()
            fail("失敗が投げられていない")
        } catch (e: FetchFailure) {
            assertEquals(FetchFailure.timeout, e)
        }
    }

    @Test
    fun `続きの取得がすべて失敗したら、読み込めている分はそのままに知らせを載せる`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), ProductListResult.Failed(ApiFailure.Offline))

        val more = model.fetchMore(model.fetch())

        assertTrue("失敗しただけで続きが無いことにされている", more is FetchMore.More)
        assertEquals("失敗したのに一覧が変わっている", listOf("a"), more.list?.products?.map { it.title })
        assertEquals(FetchFailure.offline, more.list?.notice)
    }

    @Test
    fun `捨てられた結果は続きとして積まない`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), ProductListResult.Stale)

        assertEquals("捨てられた結果が続きとして積まれている", FetchMore.Unchanged, model.fetchMore(model.fetch()))
    }

    @Test
    fun `再取得で置き換えられた続きの取得は、知らせを立てない`() = runTest(dispatcher) {
        val gate = CompletableDeferred<Unit>()
        val reloads = ArrayDeque(listOf(loaded("a", hasMore = true), loaded("x")))
        val model = HomeViewModel(
            object : ProductListing {
                override suspend fun reload(): ProductListResult = reloads.removeFirst()

                override suspend fun loadNext(): ProductListResult {
                    gate.await()
                    return degraded(ApiFailure.Offline, "a", "b")
                }
            },
        )

        model.fetchState.reload(model::fetch)
        advanceUntilIdle()
        model.fetchState.loadMore(model::fetchMore)
        runCurrent()

        model.fetchState.reload(model::fetch)
        gate.complete(Unit)
        advanceUntilIdle()

        val list = (model.fetchState.phase.value as FetchPhase.Loaded).value

        assertEquals(listOf("x"), list.products.map { it.title })
        assertNull("置き換えられた続きの取得が知らせを立てている", list.notice)
    }

    @Test
    fun `最初の取得が認証切れなら、セッションの終わりとして投げる`() = runTest(dispatcher) {
        val model = model(ProductListResult.Failed(ApiFailure.Unauthorized))

        try {
            model.fetch()
            fail("失敗が投げられていない")
        } catch (e: FetchFailure) {
            assertTrue(e.endsSession)
        }
    }

    @Test
    fun `続きの取得が認証切れなら、知らせにせずセッションの終わりとして投げる`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), degraded(ApiFailure.Unauthorized, "a"))
        val current = model.fetch()

        try {
            model.fetchMore(current)
            fail("認証切れが知らせに紛れている")
        } catch (e: FetchFailure) {
            assertTrue(e.endsSession)
        }
    }
}

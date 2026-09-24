package com.yossibank.androidapptemplate.feature.home

import androidx.lifecycle.ViewModelStore
import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.FetchMore
import com.yossibank.shared.core.ApiFailure
import com.yossibank.shared.pokemon.PokemonEntry
import com.yossibank.shared.pokemon.PokemonListResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
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
    private vararg val pages: PokemonListResult,
) : PokemonListing {
    private var index = 0

    var closed = false
        private set

    override suspend fun reload(): PokemonListResult {
        index = 0
        return next()
    }

    override suspend fun loadNext(): PokemonListResult = next()

    override fun close() {
        closed = true
    }

    private fun next(): PokemonListResult = pages[minOf(index++, pages.size - 1)]
}

private fun entries(names: Array<out String>) = names.mapIndexed { index, name ->
    PokemonEntry(id = index + 1, name = name, imageUrl = "https://img.example/${index + 1}.png")
}

private fun loaded(
    vararg names: String,
    hasMore: Boolean = false,
) = PokemonListResult.Loaded(pokemon = entries(names), hasMore = hasMore, total = 1351)

private fun degraded(
    failure: ApiFailure,
    vararg names: String,
) = PokemonListResult.Loaded(pokemon = entries(names), hasMore = true, total = 1351, failure = failure)

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

    private fun model(vararg pages: PokemonListResult) = HomeViewModel(StubListing(*pages))

    @Test
    fun `取得に成功したら一覧になる`() = runTest(dispatcher) {
        assertEquals(listOf("pikachu"), model(loaded("pikachu")).fetch().map { it.name })
    }

    @Test
    fun `続きを読むと一覧が伸びる`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), loaded("a", "b"))

        model.fetch()
        val more = model.fetchMore()

        assertEquals(listOf("a", "b"), more?.value?.map { it.name })
    }

    @Test
    fun `最後のページは終端として返す`() = runTest(dispatcher) {
        val model = model(loaded("a"))

        model.fetch()

        assertTrue("終端になっていない", model.fetchMore() is FetchMore.Last)
    }

    @Test
    fun `追加取得が一部失敗したら知らせを立て、続きがあることは残す`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), degraded(ApiFailure.Offline, "a"))

        model.fetch()
        val more = model.fetchMore()

        assertEquals("追加取得の失敗が握り潰されている", FetchFailure.offline, model.viewState.value.notice)
        assertTrue("失敗しただけで続きが無いことにされている", more is FetchMore.More)
    }

    @Test
    fun `最初の取得の失敗はそのまま失敗として投げる`() = runTest(dispatcher) {
        val model = model(PokemonListResult.Failed(ApiFailure.Timeout))

        try {
            model.fetch()
            fail("失敗が投げられていない")
        } catch (e: FetchFailure) {
            assertEquals(FetchFailure.timeout, e)
        }
    }

    @Test
    fun `続きの取得がすべて失敗したら知らせを立て、一覧には何も積まない`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), PokemonListResult.Failed(ApiFailure.Offline))

        model.fetch()

        assertNull(model.fetchMore())
        assertEquals(FetchFailure.offline, model.viewState.value.notice)
    }

    @Test
    fun `捨てられた結果は続きとして積まない`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), PokemonListResult.Stale)

        model.fetch()

        assertNull("捨てられた結果が続きとして積まれている", model.fetchMore())
    }

    @Test
    fun `全体件数が表示状態まで届く`() = runTest(dispatcher) {
        val model = model(loaded("a"))

        model.fetch()

        assertEquals(1351, model.viewState.value.total)
    }

    @Test
    fun `畳まれたら共通コアを閉じる`() {
        val stub = StubListing(loaded("a"))
        val model = HomeViewModel(stub)

        ViewModelStore()
            .apply { put("home", model) }
            .clear()

        assertTrue(stub.closed)
    }
}

package com.yossibank.androidapptemplate.feature.home

import androidx.lifecycle.ViewModelStore
import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.FetchMore
import com.yossibank.androidapptemplate.core.screen.FetchPhase
import com.yossibank.shared.core.ApiFailure
import com.yossibank.shared.pokemon.PokemonEntry
import com.yossibank.shared.pokemon.PokemonListResult
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
) = PokemonListResult.Degraded(pokemon = entries(names), hasMore = true, total = 1351, failure = failure)

private val FetchMore<PokemonList>.list: PokemonList?
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

    private fun model(vararg pages: PokemonListResult) = HomeViewModel(StubListing(*pages))

    @Test
    fun `取得に成功したら一覧になる`() = runTest(dispatcher) {
        val list = model(loaded("pikachu")).fetch()

        assertEquals(listOf("pikachu"), list.pokemon.map { it.name })
        assertEquals(1351, list.total)
        assertNull(list.notice)
    }

    @Test
    fun `続きを読むと一覧が伸びる`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), loaded("a", "b"))

        val more = model.fetchMore(model.fetch())

        assertEquals(listOf("a", "b"), more.list?.pokemon?.map { it.name })
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
        val model = model(PokemonListResult.Failed(ApiFailure.Timeout))

        try {
            model.fetch()
            fail("失敗が投げられていない")
        } catch (e: FetchFailure) {
            assertEquals(FetchFailure.timeout, e)
        }
    }

    @Test
    fun `続きの取得がすべて失敗したら、読み込めている分はそのままに知らせを載せる`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), PokemonListResult.Failed(ApiFailure.Offline))

        val more = model.fetchMore(model.fetch())

        assertTrue("失敗しただけで続きが無いことにされている", more is FetchMore.More)
        assertEquals("失敗したのに一覧が変わっている", listOf("a"), more.list?.pokemon?.map { it.name })
        assertEquals(FetchFailure.offline, more.list?.notice)
    }

    @Test
    fun `捨てられた結果は続きとして積まない`() = runTest(dispatcher) {
        val model = model(loaded("a", hasMore = true), PokemonListResult.Stale)

        assertEquals("捨てられた結果が続きとして積まれている", FetchMore.Unchanged, model.fetchMore(model.fetch()))
    }

    @Test
    fun `再取得で置き換えられた続きの取得は、知らせを立てない`() = runTest(dispatcher) {
        val gate = CompletableDeferred<Unit>()
        val reloads = ArrayDeque(listOf(loaded("a", hasMore = true), loaded("x")))
        val model = HomeViewModel(
            object : PokemonListing {
                override suspend fun reload(): PokemonListResult = reloads.removeFirst()

                override suspend fun loadNext(): PokemonListResult {
                    gate.await()
                    return degraded(ApiFailure.Offline, "a", "b")
                }

                override fun close() = Unit
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

        assertEquals(listOf("x"), list.pokemon.map { it.name })
        assertNull("置き換えられた続きの取得が知らせを立てている", list.notice)
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

package com.yossibank.androidapptemplate

import androidx.lifecycle.ViewModelStore
import com.yossibank.shared.PokemonBaseStat
import com.yossibank.shared.PokemonEntry
import com.yossibank.shared.PokemonEntryDetail
import com.yossibank.shared.PokemonFailure
import com.yossibank.shared.PokemonListResult
import com.yossibank.shared.PokemonStatKind
import com.yossibank.shared.PokemonTypeKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class StubPaging(
    private val page: suspend (Int) -> PokemonListResult,
    private val repair: suspend () -> PokemonListResult = { PokemonListResult.Loaded(emptyList(), hasMore = false, total = 0) },
) : PokemonPaging {
    var calls = 0
        private set

    var repairCalls = 0
        private set

    private var index = 0

    override suspend fun loadNext(): PokemonListResult {
        calls += 1
        return page(index++)
    }

    override suspend fun retryMissingDetails(): PokemonListResult {
        repairCalls += 1
        return repair()
    }

    override suspend fun reset() {
        index = 0
    }

    override fun close() {
        closed = true
    }

    var closed = false
        private set
}

private fun entries(
    names: Array<out String>,
    hasDetail: Boolean = true,
) = names.mapIndexed { index, name ->
    PokemonEntry(
        id = index + 1,
        name = name,
        detail = if (hasDetail) {
            PokemonEntryDetail.Loaded(
                spriteUrl = "https://img.example/${index + 1}.png",
                artworkUrl = "https://img.example/artwork/${index + 1}.png",
                types = listOf(PokemonTypeKind.GRASS),
                baseStats = listOf(PokemonBaseStat(PokemonStatKind.HP, 45)),
            )
        } else {
            PokemonEntryDetail.Missing(PokemonFailure.Server(statusCode = 500))
        },
    )
}

private fun loaded(
    vararg names: String,
    hasMore: Boolean = false,
    hasDetail: Boolean = true,
) = PokemonListResult.Loaded(
    pokemon = entries(names, hasDetail),
    hasMore = hasMore,
    total = 1351,
)

private fun degraded(
    failure: PokemonFailure,
    vararg names: String,
) = PokemonListResult.Degraded(
    pokemon = entries(names),
    hasMore = true,
    total = 1351,
    failure = failure,
)

private fun failed(failure: PokemonFailure) = PokemonListResult.Failed(failure = failure)

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(page: suspend (Int) -> PokemonListResult) = PokemonListViewModel(StubPaging(page))

    private fun names(uiState: PokemonListUiState) = (uiState as PokemonListUiState.Loaded).pokemon.map { it.name }

    @Test
    fun `取得に成功したら一覧になる`() = runTest(dispatcher) {
        val model = viewModel { loaded("pikachu") }

        advanceUntilIdle()

        assertEquals(listOf("pikachu"), names(model.uiState.value))
    }

    @Test
    fun `結果が空なら Empty になる`() = runTest(dispatcher) {
        val model = viewModel { PokemonListResult.Loaded(emptyList(), hasMore = false, total = 0) }

        advanceUntilIdle()

        assertEquals(PokemonListUiState.Empty, model.uiState.value)
    }

    @Test
    fun `続きを読むと一覧が伸びる`() = runTest(dispatcher) {
        val model = viewModel { index ->
            if (index == 0) loaded("a", hasMore = true) else loaded("a", "b", hasMore = false)
        }
        advanceUntilIdle()

        model.loadMore()
        advanceUntilIdle()

        assertEquals(listOf("a", "b"), names(model.uiState.value))
        assertFalse((model.uiState.value as PokemonListUiState.Loaded).hasMore)
    }

    @Test
    fun `終端では続きを読まない`() = runTest(dispatcher) {
        val stub = StubPaging(page = { loaded("a", hasMore = false) })
        val model = PokemonListViewModel(stub)
        advanceUntilIdle()

        val callsAtEnd = stub.calls
        model.loadMore()
        advanceUntilIdle()

        assertEquals(callsAtEnd, stub.calls)
    }

    @Test
    fun `追加取得が失敗しても読み込めた分は残る`() = runTest(dispatcher) {
        val model = viewModel { index ->
            if (index == 0) loaded("a", hasMore = true) else degraded(PokemonFailure.Offline, "a")
        }
        advanceUntilIdle()

        model.loadMore()
        advanceUntilIdle()

        assertEquals(listOf("a"), names(model.uiState.value))
    }

    @Test
    fun `追加取得の失敗は知らせに出る`() = runTest(dispatcher) {
        val model = viewModel { index ->
            if (index == 0) loaded("a", hasMore = true) else degraded(PokemonFailure.Offline, "a")
        }
        advanceUntilIdle()

        model.loadMore()
        advanceUntilIdle()

        val uiState = model.uiState.value as PokemonListUiState.Loaded
        assertEquals(R.string.error_offline, uiState.notice?.messageRes)
    }

    @Test
    fun `全体件数が表示状態まで届く`() = runTest(dispatcher) {
        val model = viewModel { loaded("a") }

        advanceUntilIdle()

        assertEquals(1351, (model.uiState.value as PokemonListUiState.Loaded).total)
    }

    @Test
    fun `詳細を取れなかった件数が出る`() = runTest(dispatcher) {
        val model = viewModel { loaded("a", "b", hasDetail = false) }

        advanceUntilIdle()

        assertEquals(2, (model.uiState.value as PokemonListUiState.Loaded).incompleteCount)
    }

    @Test
    fun `畳まれたら共通コアを閉じる`() = runTest(dispatcher) {
        val stub = StubPaging(page = { loaded("a") })
        val model = PokemonListViewModel(stub)
        advanceUntilIdle()

        ViewModelStore()
            .apply { put("pokemon", model) }
            .clear()

        assertTrue(stub.closed)
    }

    @Test
    fun `接続できないときは再試行できる失敗になる`() = runTest(dispatcher) {
        val model = viewModel { failed(PokemonFailure.Offline) }

        advanceUntilIdle()

        assertTrue((model.uiState.value as PokemonListUiState.Failed).canRetry)
    }

    @Test
    fun `応答が遅いときは接続断とは別の文言になる`() = runTest(dispatcher) {
        val model = viewModel { failed(PokemonFailure.Timeout) }

        advanceUntilIdle()

        val uiState = model.uiState.value as PokemonListUiState.Failed
        assertEquals(R.string.error_timeout, uiState.messageRes)
        assertTrue(uiState.canRetry)
    }

    @Test
    fun `サーバーエラーは状態コードを文言に含める`() = runTest(dispatcher) {
        val model = viewModel { failed(PokemonFailure.Server(503)) }

        advanceUntilIdle()

        val uiState = model.uiState.value as PokemonListUiState.Failed
        assertEquals(R.string.error_server, uiState.messageRes)
        assertEquals(listOf(503), uiState.formatArgs)
    }

    @Test
    fun `解釈できない応答は再試行できない失敗になる`() = runTest(dispatcher) {
        val model = viewModel { failed(PokemonFailure.Unexpected) }

        advanceUntilIdle()

        assertFalse((model.uiState.value as PokemonListUiState.Failed).canRetry)
    }

    @Test
    fun `再取得すると進行中の結果は捨てられる`() = runTest(dispatcher) {
        var call = 0
        val model = viewModel {
            call += 1
            if (call == 1) {
                delay(1_000)
                loaded("古い結果")
            } else {
                loaded("新しい結果")
            }
        }

        advanceTimeBy(100)
        model.reload()
        advanceUntilIdle()

        assertEquals(listOf("新しい結果"), names(model.uiState.value))
    }

    @Test
    fun `共通コアがキャンセルを飲んでも古い結果は出ない`() = runTest(dispatcher) {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))

        var call = 0
        val model = viewModel {
            call += 1
            if (call == 1) {
                try {
                    delay(1_000)
                    loaded("古い")
                } catch (e: Exception) {
                    failed(PokemonFailure.Unexpected)
                }
            } else {
                loaded("新しい")
            }
        }

        val seen = mutableListOf<PokemonListUiState>()
        val collector = launch(Dispatchers.Main) { model.uiState.collect { seen += it } }

        model.reload()
        advanceUntilIdle()
        collector.cancel()

        assertFalse(
            "キャンセルした取得の結果が観測された: $seen",
            seen.any { it is PokemonListUiState.Failed },
        )
    }

    @Test
    fun `取得が例外を投げても落ちずに失敗状態になる`() = runTest(dispatcher) {
        val model = viewModel { throw IllegalStateException("通信が死んだ") }

        advanceUntilIdle()

        val failed = model.uiState.value as PokemonListUiState.Failed

        assertEquals(R.string.error_unexpected, failed.messageRes)
        assertTrue(failed.canRetry)
    }

    @Test
    fun `詳細だけを取り直せる`() = runTest(dispatcher) {
        val stub = StubPaging(
            page = { loaded("a", "b", hasDetail = false) },
            repair = { loaded("a", "b", hasDetail = true) },
        )
        val model = PokemonListViewModel(stub)
        advanceUntilIdle()

        assertEquals(2, (model.uiState.value as PokemonListUiState.Loaded).incompleteCount)

        model.retryMissingDetails()
        advanceUntilIdle()

        assertEquals(1, stub.repairCalls)
        assertEquals("詳細の取り直しでページを読み直している", 1, stub.calls)
        assertEquals(0, (model.uiState.value as PokemonListUiState.Loaded).incompleteCount)
    }

    @Test
    fun `プルして再取得しても一覧は消えない`() = runTest(dispatcher) {
        var call = 0
        val model = viewModel {
            call += 1
            if (call == 1) loaded("a", hasMore = true) else loaded("b", hasMore = true)
        }
        advanceUntilIdle()

        val seen = mutableListOf<PokemonListUiState>()
        val collector = launch(Dispatchers.Main) { model.uiState.collect { seen += it } }

        model.refresh()
        advanceUntilIdle()
        collector.cancel()

        assertFalse(
            "再取得の途中で一覧が Loading に落ちている: $seen",
            seen.any { it is PokemonListUiState.Loading },
        )
        assertEquals(listOf("b"), names(model.uiState.value))
        assertFalse((model.uiState.value as PokemonListUiState.Loaded).isRefreshing)
    }

    @Test
    fun `取り直す詳細が無いなら共通コアに頼まない`() = runTest(dispatcher) {
        val stub = StubPaging(page = { loaded("a") })
        val model = PokemonListViewModel(stub)
        advanceUntilIdle()

        model.retryMissingDetails()
        advanceUntilIdle()

        assertEquals(0, stub.repairCalls)
    }

    @Test
    fun `捨てられた結果は表示も進行中の印も変えない`() = runTest(dispatcher) {
        val model = viewModel { index ->
            if (index == 0) loaded("a", hasMore = true) else PokemonListResult.Stale
        }
        advanceUntilIdle()

        model.loadMore()
        advanceUntilIdle()

        val uiState = model.uiState.value as PokemonListUiState.Loaded

        assertEquals(listOf("a"), uiState.pokemon.map { it.name })
        assertFalse("読み込み中の印が残っている", uiState.isLoadingMore)
    }
}

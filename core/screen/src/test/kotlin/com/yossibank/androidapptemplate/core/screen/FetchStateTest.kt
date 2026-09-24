package com.yossibank.androidapptemplate.core.screen

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

private val <T> FetchState<T>.loaded: T?
    get() = (phase.value as? FetchPhase.Loaded)?.value

private val FetchState<*>.failure: FetchFailure?
    get() = (phase.value as? FetchPhase.Failed)?.failure

@OptIn(ExperimentalCoroutinesApi::class)
class FetchStateTest {
    private fun TestScope.state() = FetchState<List<Int>>(this)

    @Test
    fun `取得に成功したら一覧になる`() = runTest {
        val state = state()

        state.reload { listOf(1, 2) }
        advanceUntilIdle()

        assertEquals(listOf(1, 2), state.loaded)
    }

    @Test
    fun `再取得を頼めば走り直す`() = runTest {
        val state = state()
        var calls = 0

        state.reload { listOf(++calls) }
        advanceUntilIdle()
        state.reload { listOf(++calls) }
        advanceUntilIdle()

        assertEquals(2, calls)
        assertEquals(listOf(2), state.loaded)
    }

    @Test
    fun `プルして再取得している間も一覧は消えない`() = runTest {
        val state = state()
        state.reload { listOf(1) }
        advanceUntilIdle()

        val gate = CompletableDeferred<Unit>()
        state.refresh {
            gate.await()
            listOf(2)
        }
        runCurrent()

        assertEquals("再取得の途中で一覧が消えている", listOf(1), state.loaded)
        assertEquals(FetchOperation.REFRESH, state.running.value)

        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals(listOf(2), state.loaded)
        assertNull(state.running.value)
    }

    @Test
    fun `何も出ていないときの再取得は通常の取得として扱う`() = runTest {
        val state = state()

        state.refresh { listOf(1) }
        advanceUntilIdle()

        assertEquals(listOf(1), state.loaded)
    }

    @Test
    fun `再取得を頼むと、進行中の取得結果は捨てられる`() = runTest {
        val state = state()
        val gate = CompletableDeferred<Unit>()

        state.reload {
            gate.await()
            listOf(1)
        }
        runCurrent()

        state.reload { listOf(2) }
        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals("置き換えられた取得の結果が上書きしている", listOf(2), state.loaded)
    }

    @Test
    fun `キャンセルを飲み込んだ取得の結果も、置き換えられた後は出さない`() = runTest {
        val state = state()
        val gate = CompletableDeferred<Unit>()

        state.reload {
            try {
                gate.await()
                listOf(1)
            } catch (_: Exception) {
                throw FetchFailure.unreadable
            }
        }
        runCurrent()

        state.reload { listOf(2) }
        advanceUntilIdle()

        assertNull(state.failure)
        assertEquals(listOf(2), state.loaded)
    }

    @Test
    fun `取得が失敗したら失敗状態になる`() = runTest {
        val state = state()

        state.reload { throw FetchFailure.timeout }
        advanceUntilIdle()

        assertEquals(FetchFailure.timeout, state.failure)
    }

    @Test
    fun `想定外の例外は再試行できる予期しない失敗になる`() = runTest {
        val state = state()

        state.reload { throw IllegalStateException("通信が死んだ") }
        advanceUntilIdle()

        assertEquals(FetchFailure.unexpected(canRetry = true), state.failure)
    }

    @Test
    fun `続きの取得が失敗しても、読み込めている分は消えない`() = runTest {
        val state = state()
        state.reload { listOf(1, 2) }
        advanceUntilIdle()

        state.loadMore { throw FetchFailure.offline }
        advanceUntilIdle()

        assertEquals(listOf(1, 2), state.loaded)
        assertNull(state.running.value)
    }

    @Test
    fun `続きが返らなかったら一覧はそのままで、続きを頼み直せる`() = runTest {
        val state = state()
        state.reload { listOf(1, 2) }
        advanceUntilIdle()

        state.loadMore { null }
        advanceUntilIdle()

        assertEquals(listOf(1, 2), state.loaded)

        state.loadMore { FetchMore.More(listOf(1, 2, 3)) }
        advanceUntilIdle()

        assertEquals("続きが返らなかった後に続きを頼み直せない", listOf(1, 2, 3), state.loaded)
    }

    @Test
    fun `続きの取得中に再取得すると、続きの結果は捨てられる`() = runTest {
        val state = state()
        state.reload { listOf(1) }
        advanceUntilIdle()

        val gate = CompletableDeferred<Unit>()
        state.loadMore {
            gate.await()
            FetchMore.More(listOf(1, 2))
        }
        runCurrent()

        state.reload { listOf(9) }
        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals(listOf(9), state.loaded)
        assertNull(state.running.value)

        state.loadMore { FetchMore.More(listOf(9, 10)) }
        advanceUntilIdle()

        assertEquals("続きを読めなくなっている", listOf(9, 10), state.loaded)
    }

    @Test
    fun `終端を受け取ったら、もう続きを取りにいかない`() = runTest {
        val state = state()
        state.reload { listOf(1) }
        advanceUntilIdle()

        state.loadMore { FetchMore.Last(listOf(1, 2)) }
        advanceUntilIdle()

        var asked = false
        state.loadMore {
            asked = true
            FetchMore.More(listOf(1, 2, 3))
        }
        advanceUntilIdle()

        assertFalse(asked)
        assertEquals(listOf(1, 2), state.loaded)
    }

    @Test
    fun `再取得すると終端の記憶は消える`() = runTest {
        val state = state()
        state.reload { listOf(1) }
        advanceUntilIdle()
        state.loadMore { FetchMore.Last(listOf(1, 2)) }
        advanceUntilIdle()

        state.reload { listOf(1) }
        advanceUntilIdle()
        state.loadMore { FetchMore.More(listOf(1, 3)) }
        advanceUntilIdle()

        assertEquals(listOf(1, 3), state.loaded)
    }

    @Test
    fun `読み込めていないうちは続きを取りにいかない`() = runTest {
        val state = state()
        var asked = false

        state.loadMore {
            asked = true
            FetchMore.More(listOf(9))
        }
        advanceUntilIdle()

        assertFalse(asked)
        assertNull(state.loaded)
    }

    @Test
    fun `プルして再取得している間は続きを取りにいかない`() = runTest {
        val state = state()
        state.reload { listOf(1) }
        advanceUntilIdle()

        val gate = CompletableDeferred<Unit>()
        state.refresh {
            gate.await()
            listOf(2)
        }
        runCurrent()

        var asked = false
        state.loadMore {
            asked = true
            FetchMore.More(listOf(2, 3))
        }
        gate.complete(Unit)
        advanceUntilIdle()

        assertFalse("再取得と続きの取得が重なっている", asked)
        assertEquals(listOf(2), state.loaded)
    }
}

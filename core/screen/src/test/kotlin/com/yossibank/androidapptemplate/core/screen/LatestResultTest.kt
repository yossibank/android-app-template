package com.yossibank.androidapptemplate.core.screen

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LatestResultTest {
    @Test
    fun `結果を状態に書く`() = runTest {
        val state = MutableStateFlow("初期")
        val latest = LatestResult(TestScope(testScheduler), state)

        latest.restart { "取得した" }
        advanceUntilIdle()

        assertEquals("取得した", state.value)
    }

    @Test
    fun `restart は進行中のものを捨てる`() = runTest {
        val state = MutableStateFlow("初期")
        val latest = LatestResult(TestScope(testScheduler), state)

        latest.restart {
            delay(1_000)
            "古い"
        }
        advanceTimeBy(100)

        latest.restart { "新しい" }
        advanceUntilIdle()

        assertEquals("新しい", state.value)
    }

    @Test
    fun `startIfIdle は進行中なら何もしない`() = runTest {
        val state = MutableStateFlow("初期")
        val latest = LatestResult(TestScope(testScheduler), state)

        latest.restart {
            delay(1_000)
            "先に始めたもの"
        }
        advanceTimeBy(100)

        latest.startIfIdle { "割り込み" }
        advanceUntilIdle()

        assertEquals("先に始めたもの", state.value)
    }

    @Test
    fun `startIfIdle は進行中でなければ始める`() = runTest {
        val state = MutableStateFlow("初期")
        val latest = LatestResult(TestScope(testScheduler), state)

        latest.startIfIdle { "取得した" }
        advanceUntilIdle()

        assertEquals("取得した", state.value)
    }

    @Test
    fun `進行中は isRunning が立つ`() = runTest {
        val state = MutableStateFlow("初期")
        val latest = LatestResult(TestScope(testScheduler), state)

        latest.restart {
            delay(1_000)
            "取得した"
        }
        advanceTimeBy(100)

        assertTrue(latest.isRunning)

        advanceUntilIdle()

        assertFalse(latest.isRunning)
    }
}

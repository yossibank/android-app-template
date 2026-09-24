package com.yossibank.androidapptemplate.core.screen

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private class CountingModel : ScreenViewModel<List<Int>>() {
    var fetchCalls = 0
        private set

    override suspend fun fetch(): List<Int> = listOf(++fetchCalls)
}

@OptIn(ExperimentalCoroutinesApi::class)
class ScreenViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `画面に戻っただけでは取得し直さない`() = runTest(dispatcher) {
        val model = CountingModel()

        model.start()
        advanceUntilIdle()
        model.start()
        advanceUntilIdle()

        assertEquals("画面に戻るたびに取得し直している", 1, model.fetchCalls)
    }

    @Test
    fun `再取得を頼めば走り直す`() = runTest(dispatcher) {
        val model = CountingModel()

        model.start()
        advanceUntilIdle()
        model.request(FetchOperation.RELOAD)
        advanceUntilIdle()

        assertEquals(2, model.fetchCalls)
    }
}

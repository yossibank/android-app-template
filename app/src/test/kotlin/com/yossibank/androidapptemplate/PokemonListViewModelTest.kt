package com.yossibank.androidapptemplate

import com.yossibank.shared.PokemonListResult
import com.yossibank.shared.generated.model.PokemonSummary
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
import org.junit.Before
import org.junit.Test

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

    @Test
    fun `取得に成功したら一覧になる`() = runTest(dispatcher) {
        val viewModel = PokemonListViewModel { loaded("pikachu") }

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(listOf("pikachu"), (uiState as PokemonListUiState.Loaded).pokemon.map { it.name })
    }

    @Test
    fun `取得に失敗したらメッセージを持つ`() = runTest(dispatcher) {
        val viewModel = PokemonListViewModel { PokemonListResult.Failed("圏外です") }

        advanceUntilIdle()

        assertEquals(PokemonListUiState.Failed("圏外です"), viewModel.uiState.value)
    }

    @Test
    fun `再取得すると進行中の結果は捨てられる`() = runTest(dispatcher) {
        var call = 0
        val viewModel = PokemonListViewModel {
            call += 1
            if (call == 1) {
                delay(1_000)
                loaded("古い結果")
            } else {
                loaded("新しい結果")
            }
        }

        advanceTimeBy(100)
        viewModel.reload()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(listOf("新しい結果"), (uiState as PokemonListUiState.Loaded).pokemon.map { it.name })
    }

    @Test
    fun `共通コアがキャンセルを飲んでも古い結果は出ない`() = runTest(dispatcher) {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))

        var call = 0
        val viewModel = PokemonListViewModel {
            call += 1
            if (call == 1) {
                try {
                    delay(1_000)
                    loaded("古い結果")
                } catch (e: Exception) {
                    PokemonListResult.Failed("キャンセルが化けた")
                }
            } else {
                loaded("新しい結果")
            }
        }

        val seen = mutableListOf<PokemonListUiState>()
        val collector = launch(Dispatchers.Main) { viewModel.uiState.collect { seen += it } }

        viewModel.reload()
        advanceUntilIdle()
        collector.cancel()

        assertFalse(
            "キャンセルした取得の結果が観測された: " + seen,
            seen.contains(PokemonListUiState.Failed("キャンセルが化けた")),
        )
    }

    @Test
    fun `取得が例外を投げても落ちずに失敗状態になる`() = runTest(dispatcher) {
        val viewModel = PokemonListViewModel { throw IllegalStateException("通信が死んだ") }

        advanceUntilIdle()

        assertEquals(PokemonListUiState.Failed("通信が死んだ"), viewModel.uiState.value)
    }

    @Test
    fun `結果が空なら Empty になる`() = runTest(dispatcher) {
        val viewModel = PokemonListViewModel {
            PokemonListResult.Loaded(pokemon = emptyList(), hasMore = false)
        }

        advanceUntilIdle()

        assertEquals(PokemonListUiState.Empty, viewModel.uiState.value)
    }

    private fun loaded(name: String) = PokemonListResult.Loaded(
        pokemon = listOf(PokemonSummary(name, "https://example.com/$name")),
        hasMore = false,
    )
}

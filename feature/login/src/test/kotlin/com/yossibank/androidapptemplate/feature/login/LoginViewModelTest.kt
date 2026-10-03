package com.yossibank.androidapptemplate.feature.login

import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.shared.auth.LoginResult
import com.yossibank.shared.core.ApiFailure
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class StubAuthenticator(
    private val result: LoginResult,
    private val gate: CompletableDeferred<Unit>? = null,
) : Authenticator {
    val attempts = mutableListOf<Pair<String, String>>()

    override suspend fun login(
        username: String,
        password: String,
    ): LoginResult {
        attempts += username to password
        gate?.await()
        return result
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
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
    fun `ログインできたらログイン済みになる`() = runTest(dispatcher) {
        val authenticator = StubAuthenticator(LoginResult.LoggedIn)
        val model = LoginViewModel(authenticator)

        model.updateUsername(" emilys ")
        model.updatePassword("emilyspass")
        model.submit()
        advanceUntilIdle()

        assertTrue(model.state.value.loggedIn)
        assertEquals(listOf("emilys" to "emilyspass"), authenticator.attempts)
    }

    @Test
    fun `拒否されたら理由を出し、ログイン済みにはしない`() = runTest(dispatcher) {
        val model = LoginViewModel(StubAuthenticator(LoginResult.Rejected))

        model.submit()
        advanceUntilIdle()

        val state = model.state.value
        assertFalse(state.loggedIn)
        assertEquals(R.string.login_rejected, state.failure?.messageRes)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `通信できなかったら共通の文言で知らせる`() = runTest(dispatcher) {
        val model = LoginViewModel(StubAuthenticator(LoginResult.Failed(ApiFailure.Offline)))

        model.submit()
        advanceUntilIdle()

        assertEquals(FetchFailure.offline, model.state.value.failure)
    }

    @Test
    fun `入力を直したら前の失敗は消える`() = runTest(dispatcher) {
        val model = LoginViewModel(StubAuthenticator(LoginResult.Rejected))
        model.submit()
        advanceUntilIdle()

        model.updatePassword("other")

        assertNull(model.state.value.failure)
    }

    @Test
    fun `ユーザー名が空なら送らない`() = runTest(dispatcher) {
        val authenticator = StubAuthenticator(LoginResult.LoggedIn)
        val model = LoginViewModel(authenticator)

        model.updateUsername("  ")
        model.submit()
        advanceUntilIdle()

        assertTrue(authenticator.attempts.isEmpty())
    }

    @Test
    fun `送信中にもう一度押しても二重に送らない`() = runTest(dispatcher) {
        val gate = CompletableDeferred<Unit>()
        val authenticator = StubAuthenticator(LoginResult.LoggedIn, gate)
        val model = LoginViewModel(authenticator)

        model.submit()
        runCurrent()
        model.submit()
        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals(1, authenticator.attempts.size)
    }
}

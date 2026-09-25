package com.yossibank.androidapptemplate.core.screen

import com.yossibank.shared.core.ApiFailure
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FetchFailureTest {
    @Test
    fun `失敗の種類が共通の文言になる`() {
        assertEquals(FetchFailure.offline, ApiFailure.Offline.toFetchFailure())
        assertEquals(FetchFailure.timeout, ApiFailure.Timeout.toFetchFailure())
        assertEquals(FetchFailure.unreadable, ApiFailure.Unreadable.toFetchFailure())
        assertEquals(FetchFailure.unexpected(canRetry = false), ApiFailure.Closed.toFetchFailure())
    }

    @Test
    fun `応答が遅いときは接続断とは別の文言になる`() {
        assertNotEquals(FetchFailure.offline.messageRes, FetchFailure.timeout.messageRes)
    }

    @Test
    fun `サーバーエラーは状態コードを文言に含める`() {
        val failure = ApiFailure.Server(503).toFetchFailure()

        assertEquals(R.string.screen_server_error, failure.messageRes)
        assertEquals(listOf(503), failure.formatArgs)
    }

    @Test
    fun `再試行できるかは共通コアの判断をそのまま使う`() {
        assertTrue(ApiFailure.Offline.toFetchFailure().canRetry)
        assertTrue(ApiFailure.Timeout.toFetchFailure().canRetry)
        assertTrue(ApiFailure.Server(503).toFetchFailure().canRetry)
        assertTrue(ApiFailure.Server(429).toFetchFailure().canRetry)
        assertFalse(ApiFailure.Server(404).toFetchFailure().canRetry)
        assertFalse(ApiFailure.Unreadable.toFetchFailure().canRetry)
        assertFalse(ApiFailure.Closed.toFetchFailure().canRetry)
    }
}

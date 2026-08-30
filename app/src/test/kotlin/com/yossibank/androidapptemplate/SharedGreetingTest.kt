package com.yossibank.androidapptemplate

import com.yossibank.shared.Greeting
import org.junit.Assert.assertTrue
import org.junit.Test

/** 共通コア（kmp-app-template）が実際に呼べていることを検証する。 */
class SharedGreetingTest {
    @Test
    fun greeting_comes_from_shared_module() {
        val actual = Greeting().greet()
        assertTrue("actual: $actual", actual.startsWith("Hello, Android"))
    }
}

package com.yossibank.androidapptemplate

import com.yossibank.shared.Greeting
import org.junit.Assert.assertTrue
import org.junit.Test

class SharedGreetingTest {
    @Test
    fun greeting_comes_from_shared_module() {
        val actual = Greeting().greet()
        assertTrue("actual: $actual", actual.startsWith("Hello, Android"))
    }
}

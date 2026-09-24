package com.yossibank.androidapptemplate.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LatestResult<T>(
    private val scope: CoroutineScope,
    private val state: MutableStateFlow<T>,
) {
    private var job: Job? = null

    val isRunning: Boolean
        get() = job?.isActive == true

    fun restart(produce: suspend () -> T) {
        job?.cancel()
        job = write(produce)
    }

    fun startIfIdle(produce: suspend () -> T) {
        if (isRunning) return

        job = write(produce)
    }

    private fun write(produce: suspend () -> T): Job = scope.launch {
        val value = produce()

        ensureActive()
        state.value = value
    }
}

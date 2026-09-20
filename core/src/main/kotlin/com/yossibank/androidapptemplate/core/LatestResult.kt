package com.yossibank.androidapptemplate.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 最後に始めた取得の結果だけを状態に書く。
 *
 * 取り消された取得の結果を書かないための関門は、呼び出し側ではなくここにある。
 * 共通コアが CancellationException を握りつぶしても、構造化並行性だけでは
 * 古い結果が残るため、書き込み直前の ensureActive() が要る。
 */
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

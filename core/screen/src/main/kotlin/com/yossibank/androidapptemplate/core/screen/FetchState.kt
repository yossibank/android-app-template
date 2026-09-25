package com.yossibank.androidapptemplate.core.screen

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FetchState<T>(
    private val scope: CoroutineScope,
) {
    private val mutablePhase = MutableStateFlow<FetchPhase<T>>(FetchPhase.Idle)
    val phase: StateFlow<FetchPhase<T>> = mutablePhase.asStateFlow()

    private val mutableRunning = MutableStateFlow<FetchOperation?>(null)
    val running: StateFlow<FetchOperation?> = mutableRunning.asStateFlow()

    private var job: Job? = null
    private var reachedEnd = false

    fun reload(work: suspend () -> T) {
        reachedEnd = false
        mutablePhase.value = FetchPhase.Loading

        replace(FetchOperation.RELOAD, work)
    }

    fun refresh(work: suspend () -> T) {
        if (mutablePhase.value !is FetchPhase.Loaded) {
            reload(work)
            return
        }

        reachedEnd = false

        replace(FetchOperation.REFRESH, work)
    }

    fun loadMore(work: suspend () -> FetchMore<T>?) {
        if (mutablePhase.value !is FetchPhase.Loaded || reachedEnd) return

        run(FetchOperation.LOAD_MORE, replacing = false, work) { result ->
            when (result) {
                is FetchMore.More -> mutablePhase.value = FetchPhase.Loaded(result.value)

                is FetchMore.Last -> {
                    reachedEnd = true
                    mutablePhase.value = FetchPhase.Loaded(result.value)
                }

                null -> Unit
            }
        }
    }

    private fun replace(
        operation: FetchOperation,
        work: suspend () -> T,
    ) {
        run(
            operation,
            replacing = true,
            work,
            failed = { mutablePhase.value = FetchPhase.Failed(it) },
        ) { mutablePhase.value = FetchPhase.Loaded(it) }
    }

    private fun <R> run(
        operation: FetchOperation,
        replacing: Boolean,
        work: suspend () -> R,
        failed: (FetchFailure) -> Unit = {},
        succeeded: (R) -> Unit,
    ) {
        if (replacing) {
            job?.cancel()
        } else if (mutableRunning.value != null) {
            return
        }

        mutableRunning.value = operation

        job = scope.launch {
            val outcome = try {
                Result.success(work())
            } catch (e: CancellationException) {
                throw e
            } catch (e: FetchFailure) {
                Result.failure(e)
            } catch (_: Exception) {
                Result.failure(FetchFailure.unexpected(canRetry = true))
            }

            ensureActive()

            mutableRunning.value = null

            outcome.fold(succeeded) { failed(it as FetchFailure) }
        }
    }
}

package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-6.2 코루틴 취소 시 사용되는 JobCancellationException */
fun main() = runBlocking<Unit> {
    val job = launch {
        delay(1000L)
    }

    job.invokeOnCompletion { exception ->
        println(exception)
    }
    job.cancel()
}

/** 결과:
    kotlinx.coroutines.JobCancellationException: StandaloneCoroutine was cancelled; job=StandaloneCoroutine{Cancelled}@3cd1f1c8
**/
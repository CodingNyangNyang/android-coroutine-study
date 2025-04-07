package org.example.w03_chapter7.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    val rootJob = Job()
    launch(CoroutineName("Coroutine1") + rootJob) {
        launch(CoroutineName("Coroutine3")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }

        launch(CoroutineName("Coroutine4")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
    }

    launch(CoroutineName("Coroutine2") + rootJob) {
        launch(CoroutineName("Coroutine5") + Job()) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
    }

    delay(50L)
    rootJob.cancel()
    delay(1000L)
}

/** 결과:
    [main @Coroutine5#6] 코루틴 실행 완료
**/
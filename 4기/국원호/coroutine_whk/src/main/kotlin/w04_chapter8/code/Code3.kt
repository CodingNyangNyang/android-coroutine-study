package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-2.1.2 Job 객체를 사용한 예외 전파 제한의 한계 */
fun main() = runBlocking<Unit> {
    val parentJob = launch(CoroutineName("Parent Coroutine")) {
        launch(CoroutineName("Coroutine1") + Job()) {
            launch(CoroutineName("Coroutine3")) {
                delay(100L)
                println("[${Thread.currentThread().name}] 코루틴 실행")
            }
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }

        launch(CoroutineName("Coroutine2")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }

    delay(20L)
    parentJob.cancel()
    delay(1000L)
}

/** 결과:
    [main @Coroutine1#3] 코루틴 실행
    [main @Coroutine3#5] 코루틴 실행
**/
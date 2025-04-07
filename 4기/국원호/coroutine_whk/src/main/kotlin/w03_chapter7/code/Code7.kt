package org.example.w03_chapter7.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    launch(CoroutineName("Coroutine1")) Coroutine1@ {
        launch(CoroutineName("Coroutine3")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }

        launch(CoroutineName("Coroutine4")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }

        this@Coroutine1.cancel()
    }

    launch(CoroutineName("Coroutine2")) {
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
}

/** 결과:
    [main @Coroutine2#3] 코루틴 실행 완료
*/
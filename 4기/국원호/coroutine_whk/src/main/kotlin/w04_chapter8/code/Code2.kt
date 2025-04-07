package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-2.1.1 Job 객체를 사용해 예외 전파 제한하기 */
fun main() = runBlocking<Unit> {
    launch(CoroutineName("Parent Coroutine")) {
        launch(CoroutineName("Coroutine1") + Job()) {
            launch(CoroutineName("Coroutine3")) {
                throw Exception("예외 발생")
            }
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }

        launch(CoroutineName("Coroutine2")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }

    delay(1000L)
}

/** 결과:
    Exception in thread "main @Coroutine1#3" java.lang.Exception: 예외 발생
    ...
    [main @Coroutine2#4] 코루틴 실행
**/
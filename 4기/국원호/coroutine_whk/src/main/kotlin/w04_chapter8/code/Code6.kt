package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-2.2.3 SupervisorJob을 CoroutineScope와 함께 사용하기 */
fun main() = runBlocking<Unit> {
    val coroutineScope = CoroutineScope(SupervisorJob())
    coroutineScope.apply {
        launch(CoroutineName("Coroutine1")) {
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
    [DefaultDispatcher-worker-1 @Coroutine2#3] 코루틴 실행
**/
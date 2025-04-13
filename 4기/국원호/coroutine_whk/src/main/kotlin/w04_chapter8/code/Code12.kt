package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-3.4.2 SupervisorJob과 CoroutineExceptionHandler 함께 사용하기 */
fun main() = runBlocking<Unit> {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("[예외 발생] ${throwable}")
    }

    val supervisedScope = CoroutineScope(SupervisorJob() + exceptionHandler)
    supervisedScope.apply {
        launch(CoroutineName("Coroutine1")) {
            throw Exception("Coroutine1에 예외가 발생했습니다.")
        }

        launch(CoroutineName("Coroutine2")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }
    delay(1000L)
}

/** 결과:
    [예외 발생] java.lang.Exception: Coroutine1에 예외가 발생했습니다.
    [DefaultDispatcher-worker-2] 코루틴 실행
**/
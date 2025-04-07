package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-3.3 처리되지 않은 예외만 처리하는 CoroutineExceptionHandler */
fun main() = runBlocking<Unit> {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("[예외 발생] ${throwable}")
    }

    launch(CoroutineName("Coroutine1") + exceptionHandler) {
        throw Exception("Coroutine1에 예외가 발생했습니다.")
    }
    delay(1000L)
}

/** 결과:
    Exception in thread "main" java.lang.Exception: Coroutine1에 예외가 발생했습니다.
    ...
**/
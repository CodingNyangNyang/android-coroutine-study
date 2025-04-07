package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-3.5 CoroutineExceptionHandler는 예외 전파를 제한하지 않는다. */
fun main() = runBlocking<Unit> {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("[예외 발생] ${throwable}")
    }

    launch(CoroutineName("Coroutine1") + exceptionHandler) {
        throw Exception("Coroutine1에 예외가 발생했습니다.")
    }
}

/** 결과:
    [예외 발생] java.lang.Exception: Coroutine1에 예외가 발생했습니다.
**/
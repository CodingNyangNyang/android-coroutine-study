package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-5.2 async의 예외 전파 */
fun main() = runBlocking<Unit> {
    async(CoroutineName("Coroutine1")) {
        throw Exception("Coroutine1에 예외가 발생했습니다.")
    }
    launch(CoroutineName("Coroutine2")) {
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
}

/** 결과:
    Exception in thread "main" java.lang.Exception: Coroutine1에 예외가 발생했습니다.
**/
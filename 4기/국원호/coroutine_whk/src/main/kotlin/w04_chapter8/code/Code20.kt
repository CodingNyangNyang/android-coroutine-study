package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-6.3 withTimeOut 사용해 코루틴의 실행 시간 제한하기. */
fun main() = runBlocking<Unit>(CoroutineName("Parent Coroutine")) {
    launch(CoroutineName("Child Coroutine")) {
        try {
            withTimeout(1000L) {
                delay(2000L)
                println("[${Thread.currentThread().name}] 코루틴 실행")
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    delay(2000L)
    println("[${Thread.currentThread().name}] 코루틴 실행")
}

/** 결과:
    kotlinx.coroutines.TimeoutCancellationException: Timed out waiting for 1000 ms
    [main @Parent Coroutine#1] 코루틴 실행
**/
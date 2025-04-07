package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-4.1 try catch문을 사용해 코루틴 예외 처리하기 */
fun main() = runBlocking<Unit> {
    launch(CoroutineName("Coroutine1")) {
        try {
            throw Exception("Coroutine1에 예외가 발생했습니다.")
        } catch (e: Exception) {
            println(e.message)
        }
    }
    launch(CoroutineName("Coroutine2")) {
        delay(100L)
        println("Coroutine2 실행 완료")
    }
}

/** 결과:
    Coroutine1에 예외가 발생했습니다.
    Coroutine2 실행 완료
**/
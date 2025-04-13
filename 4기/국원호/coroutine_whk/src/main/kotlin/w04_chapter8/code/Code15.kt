package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-4.2 코루틴 빌더 함수에 대한 try catch문은 코루틴의 예외를 잡지 못한다 */
fun main() = runBlocking<Unit> {
    try {
        launch(CoroutineName("Coroutine1")) {
            throw Exception("Coroutine1에 예외가 발생했습니다.")
        }
    } catch (e: Exception) {
        println(e.message)
    }

    launch(CoroutineName("Coroutine2")) {
        delay(100L)
        println("Coroutine2 실행 완료")
    }
}

/** 결과:
    Exception in thread "main" java.lang.Exception: Coroutine1에 예외가 발생했습니다.
**/
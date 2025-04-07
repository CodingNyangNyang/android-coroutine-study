package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-2.3 supervisorScope를 사용한 예외 전파 제한 */
fun main() = runBlocking<Unit> {
    supervisorScope {
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
}

/** 결과:
    Exception in thread "main @Coroutine1#3" java.lang.Exception: 예외 발생
    ...
    [main @Coroutine2#3] 코루틴 실행
**/
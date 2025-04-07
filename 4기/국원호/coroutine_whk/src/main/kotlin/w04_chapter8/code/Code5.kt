package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-2.2.2 코루틴의 구조화를 깨지 않고 SupervisorJob 사용하기 */
fun main() = runBlocking<Unit> {
    val supervisorJob = SupervisorJob(parent = this.coroutineContext[Job])
    launch(CoroutineName("Coroutine1") + supervisorJob) {
        launch(CoroutineName("Coroutine3")) {
            throw Exception("예외 발생")
        }
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }

    launch(CoroutineName("Coroutine2") + supervisorJob) {
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
    supervisorJob.complete()
}

/** 결과:
    Exception in thread "main @Coroutine1#3" java.lang.Exception: 예외 발생
    ...
    [main @Coroutine2#3] 코루틴 실행
**/
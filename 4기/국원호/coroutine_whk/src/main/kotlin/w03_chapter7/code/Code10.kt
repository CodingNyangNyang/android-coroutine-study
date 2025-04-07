package org.example.w03_chapter7.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    launch(CoroutineName("Coroutine1")) {
        val newJob = Job()

        launch(CoroutineName("Coroutine2") + newJob) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }

    launch(CoroutineName("Coroutine3")) {
        val coroutine3Job = this.coroutineContext[Job]
        val newJob = Job(coroutine3Job)

        launch(CoroutineName("Coroutine4") + newJob) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }

    delay(1000L)
}

/** 결과:
    [main @Coroutine2#4] 코루틴 실행
    [main @Coroutine4#5] 코루틴 실행
    ...
    프로세스가 종료되지 않는다.
**/
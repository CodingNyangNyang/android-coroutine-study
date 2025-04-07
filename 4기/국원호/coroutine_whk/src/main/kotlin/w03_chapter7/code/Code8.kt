package org.example.w03_chapter7.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    val newScope = CoroutineScope(Dispatchers.IO)
    newScope.launch(CoroutineName("Coroutine1")) {
        launch(CoroutineName("Coroutine3")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }

        launch(CoroutineName("Coroutine4")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
    }

    newScope.launch(CoroutineName("Coroutine2")) {
        launch(CoroutineName("Coroutine5")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
    }
}

/** 결과:
    종료 코드 0(으)로 완료된 프로세스
**/
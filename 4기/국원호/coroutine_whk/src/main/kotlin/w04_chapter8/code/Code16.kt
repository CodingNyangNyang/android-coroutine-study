package org.example.w04_chapter8.code

import kotlinx.coroutines.*

/** 8-5.1 async의 예외 노출 */
fun main() = runBlocking<Unit> {
    supervisorScope {
        val deferred: Deferred<String> = async(CoroutineName("Coroutine1")) {
            throw Exception("Coroutine1에 예외가 발생했습니다.")
        }

        try {
            deferred.await()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}

/** 결과:
    Coroutine1에 예외가 발생했습니다.
**/
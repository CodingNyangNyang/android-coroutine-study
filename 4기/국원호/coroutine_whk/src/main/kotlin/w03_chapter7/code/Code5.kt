package org.example.w03_chapter7.code

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CustomCoroutineScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + newSingleThreadContext("CustomScopeThread")
}

fun main() {
    /** 커스텀 CoroutineScope 사용하기 */
    println("# [커스텀 CoroutineScope 사용하기] #")
    val coroutineScope1 = CustomCoroutineScope()
    coroutineScope1.launch {
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
    Thread.sleep(200L)

    println("\n# [CoroutineScope 생성 함수 사용하기] #")
    val coroutineScope2 = CoroutineScope(Dispatchers.IO)
    coroutineScope2.launch {
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
    Thread.sleep(200L)
}

/** 결과:
    # [커스텀 CoroutineScope 사용하기] #
    [CustomScopeThread @coroutine#1] 코루틴 실행 완료

    # [CoroutineScope 생성 함수 사용하기] #
    [DefaultDispatcher-worker-1 @coroutine#2] 코루틴 실행 완료
*/
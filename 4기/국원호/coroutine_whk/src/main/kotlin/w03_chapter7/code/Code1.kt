package org.example.w03_chapter7.code

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    println("[${Thread.currentThread().name}] 루트 코루틴 실행")
    val coroutineContext = newSingleThreadContext("MyThread") + CoroutineName("CoroutineA")

    /** 부모 코루틴 실행 환경 상속 */
    println("\n[부모 코루틴 실행 환경 상속]")
    launch(coroutineContext) {
        println("[${Thread.currentThread().name}] 부모 코루틴 실행")
        launch {
            println("[${Thread.currentThread().name}] 자식 코루틴 실행")
        }
    }.join()

    /** 부모 코루틴에 실행 환경 덮어씌우기 */
    println("\n[부모 코루틴에 실행 환경 덮어씌우기]")
    launch(coroutineContext) {
        println("[${Thread.currentThread().name}] 부모 코루틴 실행")
        launch(CoroutineName("ChildCoroutine")) {
            println("[${Thread.currentThread().name}] 자식 코루틴 실행")
        }
    }.join()
}

/** 결과:
    [main @coroutine#1] 루트 코루틴 실행

    [부모 코루틴 실행 환경 상속]
    [MyThread @CoroutineA#2] 부모 코루틴 실행
    [MyThread @CoroutineA#3] 자식 코루틴 실행

    [부모 코루틴에 실행 환경 덮어씌우기]
    [MyThread @CoroutineA#4] 부모 코루틴 실행
    [MyThread @ChildCoroutine#5] 자식 코루틴 실행
*/
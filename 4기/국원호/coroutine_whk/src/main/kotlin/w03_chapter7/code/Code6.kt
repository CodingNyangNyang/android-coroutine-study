package org.example.w03_chapter7.code

import kotlinx.coroutines.*

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val newScope = CoroutineScope(CoroutineName("MyCoroutine") + Dispatchers.IO)
    newScope.launch(CoroutineName("LaunchCoroutine")) ChildLaunch1@ {
        println(this.coroutineContext[CoroutineName])
        println(this.coroutineContext[CoroutineDispatcher])

        val launchJob = this@ChildLaunch1.coroutineContext[Job]
        val newScopeJob = newScope.coroutineContext[Job]
        println("launchJob?.parent === newScopeJob >> ${launchJob?.parent === newScopeJob}")

        println("\n# [상속 받는 계층 구조] #")
        launch ChildLaunch2@ {
            println("ChildLaunch2 CoroutineName : ${this@ChildLaunch2.coroutineContext[CoroutineName]}")
            println("ChildLaunch2NewCoroutineThread : ${Thread.currentThread().name}")
        }.join()

        println("\n# [새로운 계층 구조] #")
        CoroutineScope(CoroutineName("NewCoroutine") + newSingleThreadContext("NewThread")).launch {
            println("NewCoroutineName : ${coroutineContext[CoroutineName]}")
            println("NewCoroutineThread : ${Thread.currentThread().name}")
        }.join()
    }

    Thread.sleep(1000L)
}

/** 결과:
    CoroutineName(LaunchCoroutine)
    Dispatchers.IO
    launchJob?.parent === newScopeJob >> true

    # [상속 받는 계층 구조] #
    ChildLaunch2 CoroutineName : CoroutineName(LaunchCoroutine)
    ChildLaunch2NewCoroutineThread : DefaultDispatcher-worker-1 @LaunchCoroutine#2

    # [새로운 계층 구조] #
    NewCoroutineName : CoroutineName(NewCoroutine)
    NewCoroutineThread : NewThread @NewCoroutine#3
 */
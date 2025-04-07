package org.example

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    /** 시작 시간 등록 */
    val startTime = System.currentTimeMillis()

    /** 플랫폼1에서 등록한 관람객 목록을 가져오는 코루틴 */
    val participantDeferred1: Deferred<Array<String>> = async(Dispatchers.IO) {
        delay(1000L)
        return@async arrayOf("enso", "whk")
    }

    /** 플랫폼2에서 등록한 관람객 목록을 가져오는 코루틴 */
    val participantDeferred2: Deferred<Array<String>> = async(Dispatchers.IO) {
        delay(1000L)
        return@async arrayOf("kukwonho")
    }
    val participant1: Array<String> = participantDeferred1.await()
    val participant2: Array<String> = participantDeferred2.await()

    println("[지난 시간: ${System.currentTimeMillis() - startTime}ms]")
    println("참여자 목록 : ${listOf(*participant1, *participant2)}")
}

fun getElapsedTime(startTime: Long): String {
    return "지난 시간 : ${System.currentTimeMillis() - startTime}ms"
}

fun printJobState(job: Job) {
    println("isActivity >> ${job.isActive}")
    println("isCancelled >> ${job.isCancelled}")
    println("isCompleted >> ${job.isCancelled}")
}
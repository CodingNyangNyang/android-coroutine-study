package org.example.w03_chapter7.code

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.getElapsedTime
import org.example.printJobState

fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()

    println("# [부모 코루틴의 자식 코루틴에 대한 완료 의존성] #")
    /** 부모 코루틴 실행 */
    val parentJob = launch {
        /** 자식 코루틴 실행 */
        launch {
            delay(1000L)
            println("[${getElapsedTime(startTime)}] 자식 코루틴 실행 완료")
        }

        println("[${getElapsedTime(startTime)}] 부모 코루틴이 실행하는 마지막 코드")
    }

    /** 부모 코루틴 실행 완료/취소 완료 콜백 등록 */
    parentJob.invokeOnCompletion {
        println("[${getElapsedTime(startTime)}] 부모 코루틴 실행 완료")
    }

    delay(500L)
    printJobState(parentJob)
}

/** 결과:
    # [부모 코루틴의 자식 코루틴에 대한 완료 의존성] #
    [지난 시간 : 7ms] 부모 코루틴이 실행하는 마지막 코드
    isActivity >> true
    isCancelled >> false
    isCompleted >> false
    [지난 시간 : 1028ms] 자식 코루틴 실행 완료
    [지난 시간 : 1029ms] 부모 코루틴 실행 완료
*/
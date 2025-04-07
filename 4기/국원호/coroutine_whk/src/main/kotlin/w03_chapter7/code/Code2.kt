package org.example.w03_chapter7.code

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    /** 부모 코루틴의 CoroutineContext 부터 부모 코루틴의 Job 추출 */
    val runBlockingJob = coroutineContext[Job]

    println("[부모 코루틴으로 부터 상속되지 않는 Job]")
    /** 자식 코루틴 생성 */
    launch {
        /** 자식 코루틴의 CoroutineContext 부터 자식 코루틴의 Job 추출 */
        val launchJob = coroutineContext[Job]

        if (runBlockingJob === launchJob) {
            println("runBlocking으로 생성된 Job과 launch로 생성된 Job이 동일합니다.")
        } else {
            println("runBlocking으로 생성된 Job과 launch로 생성된 Job이 다릅니다.")
        }
    }.join()

    println("\n[부모 코루틴과 자식 코루틴의 parent/children 프로퍼티 관계]")
    /** 부모 코루틴의 CoroutineContext 부터 부모 코루틴의 Job 추출 */
    val parentJob = runBlockingJob
    /** 자식 코루틴 생성 */
    launch {
        /** 자식 코루틴의 CoroutineContext 부터 자식 코루틴의 Job 추출 */
        val childJob = coroutineContext[Job]

        println("1. 부모 코루틴과 자식 코루틴의 Job은 같은가? ${parentJob === childJob}")
        println("2. 자식 코루틴의 Job이 가지고 있는 parent는 부모 코루틴의 Job인가? ${childJob?.parent === parentJob}")
        println("3. 부모 코루틴의 Job은 자식 코루틴의 Job에 대한 참조를 가지는가? ${parentJob?.children?.contains(childJob)}")
    }
}

/** 결과:
    [부모 코루틴으로 부터 상속되지 않는 Job]
    runBlocking으로 생성된 Job과 launch로 생성된 Job이 다릅니다.

    [부모 코루틴과 자식 코루틴의 parent/children 프로퍼티 관계]
    1. 부모 코루틴과 자식 코루틴의 Job은 같은가? false
    2. 자식 코루틴의 Job이 가지고 있는 parent는 부모 코루틴의 Job인가? true
    3. 부모 코루틴의 Job은 자식 코루틴의 Job에 대한 참조를 가지는가? true
*/
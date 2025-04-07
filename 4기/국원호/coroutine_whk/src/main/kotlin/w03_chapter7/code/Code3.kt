package org.example.w03_chapter7.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    println("[3개의 데이터베이스로 부터 데이터를 가져와 실행]")
    searchDBJob().join()

    println("\n[3개의 데이터베이스로 부터 데이터를 가져와 실행중 취소]")
    searchDBJob().cancel()
}

fun CoroutineScope.searchDBJob(): Job = launch(Dispatchers.IO) {
    val dbResultDeferred: List<Deferred<String>> = listOf("db1", "db2", "db3").map {
        async {
            delay(1000L)
            println("${it}으로부터 데이터를 가져오는데 성공 했습니다.")
            return@async "[${it} data]"
        }
    }

    val dbResult: List<String> = dbResultDeferred.awaitAll()

    println(dbResult)
}

/** 결과:
    [3개의 데이터베이스로 부터 데이터를 가져와 실행]
    db2으로부터 데이터를 가져오는데 성공 했습니다.
    db1으로부터 데이터를 가져오는데 성공 했습니다.
    db3으로부터 데이터를 가져오는데 성공 했습니다.
    [[db1 data], [db2 data], [db3 data]]

    [3개의 데이터베이스로 부터 데이터를 가져와 실행중 취소]
*/
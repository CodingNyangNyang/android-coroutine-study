> '정석준님 (https://github.com/sjjeong)'께서 주관하는 Kotlin Coroutine 스터디 진행 과정<br>
**코틀린 코루틴의 정석** 책을 읽고 이해한 내용을 바탕으로 정리하여 글을 작성하였습니다. 부족한 부분이나, 틀린 부분이 있다면 반영할 수 있도록 하겠습니다.

## 5장 - async와 Deferred

### 결과를 반환하는 코루틴
launch 코루틴 빌더를 통해 생성한 코루틴의 결과는 코루틴 객체인 Job이 반환된다.
**async**를 사용하면 결과값이 있는 코루틴 객체인 **Deferred**가 반환된다.

### async 사용해 Deferred 만들기
```kotlin
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job

public fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T>
```
launch 코루틴 빌더와 async 코루틴 빌더의 선언부는 매우 비슷합니다.
파라미터로 전달받는 인자들은 동일한것을 볼 수 있습니다.

lauch : **Job**
async : **Deferred**<제네릭 타입 T>
반환 하는 타입이 서로 다른것을 볼 수 있습니다.

```kotlin
val networkDeferred: Deferred<String> = async(Dispatchers.IO) {
	delay(1000L)
    return@async "Dummy Response"
}
```
async 코루틴 빌더는 네트워크 요청을 위해 Dispatchers.IO를 사용해 1초 지연 후 **"Dummy Response"**를 결과로 반환하는 코루틴을 만들 수 있습니다.
"Dummy Response"는 String 타입이므로 Deferred의 제네릭 타입은 String 으로 선언한 **Deferred 객체**를 생성했습니다.

### await를 사용한 결괏값 수신
Deferred 객체는 결괏값 수신의 대기를 위해 await 함수를 제공한다.

Deferred 객체의 await 함수는 코루틴이 실행 완료될 때까지 호출부의 코루틴을 일시 중단한다는 점에서 Job 객체의 join 함수와 유사하게 동작한다.

```kotlin
fun main() = runBlocking<Unit> {
  val networkDeferred: Deferred<String> = async(Dispatchers.IO) {
    delay(1000L) // 네트워크 요청
    return@async "Dummy Response" // 결과값 반환
  }
  
  val result = networkDeferred.await() // networkDeferred로부터 결과값이 반환될 때까지 runBlocking 일시 중단
  println(result) // Dummy Response 출력
}

/**
	결과
    Dummy Response
*/
```

### Deferred는 특수한 형태의 Job이다
```kotlin
public interface Deferred<out T> : Job {

    public suspend fun await(): T

    public val onAwait: SelectClause1<T>

    @ExperimentalCoroutinesApi
    public fun getCompleted(): T

    @ExperimentalCoroutinesApi
    public fun getCompletionExceptionOrNull(): Throwable?
}
```
**Deferred**는 인터페이스로 정의되어 있으며, 이는 Job 인터페이스를 확장하고 있습니다.
즉 Deferred 인터페이스는 Job 인터페이스의 서브타입이고, 앞서 사용한 await 함수는 코루틴으로부터 결괏값을 반환받으려고 Deferred에 추가된 함수임을 확인할 수 있습니다.

이런 특성 때문에 Deferred 객체는 Job 객체의 모든 함수와 프로퍼티를 사용할 수 있다.
Join을 사용해 Deferred 객체가 완료 될때 까지 호출부의 코루틴을 일시 중단 할 수도 있고, 취소가 되어야할 때 cancel 함수를 호출해서 취소할 수 있습니다. 또한 상태 조회를 위해 isActivity, isCancelled, isCompleted 와 같은 프로퍼티들을 사용할 수 있습니다.


### 복수의 코루틴으로 부터 결괏값 수신하기
콘서트 개최 시 2개의 플랫폼에서 관람객을 모집한다고 가정하고, 각 플랫폼에서 관람객을 조회하고 출력하는 코드를 작성 해본다면 아래와 같은 코드가 됩니다.

```kotlin
fun main() = runBlocking<Unit> {
    /** 시작 시간 등록 */
    val startTime = System.currentTimeMillis()

    /** 플랫폼1에서 등록한 관람객 목록을 가져오는 코루틴 */
    val participantDeferred1: Deferred<Array<String>> = async(Dispatchers.IO) {
        delay(1000L)
        return@async arrayOf("enso", "whk")
    }
    val participant1: Array<String> = participantDeferred1.await()

    /** 플랫폼2에서 등록한 관람객 목록을 가져오는 코루틴 */
    val participantDeferred2: Deferred<Array<String>> = async(Dispatchers.IO) {
        delay(1000L)
        return@async arrayOf("kukwonho")
    }
    val participant2: Array<String> = participantDeferred2.await()

    println("[지난 시간: ${System.currentTimeMillis() - startTime}ms]")
    println("참여자 목록 : ${listOf(*participant1, *participant2)}")
}

/**
	결과
    [지난 시간: 2017ms]
	참여자 목록 : [enso, whk, kukwonho]
*/
```

각 플랫폼에서 데이터를 조회 하는데 1초가 걸리기 때문에 걸린 시간이 2,017ms 인것을 확인할 수 있습니다. 여기에서 ~~_동시에 데이터를 조회 하는 것 같은데_~~ 왜 2초가 걸리지 할 수 있다.

이유는 **await()** 함수를 호출 할 때 결과값이 반환 될 때까지 호출부의 코루틴이 일시 중단 되기 때문이다. 코드를 보면 participantDeferred1 의 결과를 수신할 때 까지 코루틴이 일시중단되기 때문에 다음 코드가 동작되지 않는다. 

```kotlin
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
/**
	결과
    [지난 시간: 1019ms]
	참여자 목록 : [enso, whk, kukwonho]
*/
```

코드를 조금 수정해서 await() 호출의 위치를 관람객 데이터를 조회하는 Deferred 생성 이후로 바꿔본다면 시간이 1,019ms 가 나온것을 확인 해볼 수 있다.

이 이유는 await()가 호출 되기 전 async 호출로 Deferred가 생성 되고
async 호출 시 **start: CoroutineStart = CoroutineStart.DEFAULT** 이기 때문에 코루틴이 생성되는 즉시 실행되기 때문입니다. 

### awaitAll을 사용한 결괏값 수신
위 상황에서는 2개의 플랫폼이 존재 했었는데 만약 엄청나게 많은 플랫폼이 존재한다면?
```kotlin 
...
val participant1: Array<String> = participantDeferred1.await()
val participant2: Array<String> = participantDeferred2.await()
...
val participant100: Array<String> = participantDeferred100.await()
```

존재하는 모든 Deferred에 대해서 await()를 호출해야하므로 코드가 굉장히 길어질 수 있습니다.
이를 해결 하기 위해서 코루틴 라이브러리는 **awaitAll()** 이라는 함수를 제공합니다.

```kotlin
public suspend fun <T> awaitAll(vararg deferreds: Deferred<T>): List<T>
```

awaitAll 함수는 가변 인자로 Deferred 타입의 객체를 받아 인자로 받은 모든 Deferred 코루틴으로부터 결과가 수신될 때까지 호출부의 코루틴을 일시 중단한 후 결과가 모두 수신되면 Deferred 코루틴들로부터 수신한 결괏값들을 List로 만들어 반환하고 호출부의 코루틴을 재개합니다.

앞서 설명드린 2개의 플랫폼에서 관람객을 불러오는 코드를 awaitAll 함수를 사용하도록 한다면
```kotlin
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
    val results: List<Array<String>> = awaitAll(participantDeferred1, participantDeferred2)

    println("[지난 시간: ${System.currentTimeMillis() - startTime}ms]")
    println("참여자 목록 : ${listOf(*results[0], *results[1])}")
}
/**
	결과
    [지난 시간: 1018ms]
	참여자 목록 : [enso, whk, kukwonho]
*/
```

#### 컬렉션에 대해 awiatAll 사용하기
코루틴 라이브러리는 awaitAll 함수를 Collection 인터페이스에 대한 확장 함수로도 제공합니다.

```kotlin
public suspend fun <T> Collection<Deferred<T>>.awaitAll(): List<T>

fun main() = runBlocking<Unit> {
  ...
  val results: List<Array<String>> = listOf(participantDeferred1, participantDeferred2).awaitAll()
  ...
}
```
위와 같이 구현할 수 있습니다. 

### WithContext로 async-await 대체하기
```kotlin
public suspend fun <T> withContext(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> T
): T
```
withContext 함수가 호출되면 함수의 인자로 설정된 CoroutineContext 객체를 사용해 block 람다식을 실행하고, 완료되면 그 결과를 반환합니다.

```kotlin
fun main() = runBlocking<Unit> {
  val networkDeferred: Deferred<String> = async(Dispatchers.IO) {
    delay(1000L) // 네트워크 요청
    return@async "Dummy Response" // 결과값 반환
  }
  
  val result = networkDeferred.await() // networkDeferred로부터 결과값이 반환될 때까지 runBlocking 일시 중단
  println(result) // Dummy Response 출력
}

/** async-await 쌍 -> withContext로 변경 */
fun main() = runBlocking<Unit> {
    val result = withContext(Dispatchers.IO) {
        delay(1000L) // 네트워크 요청
        return@withContext "Dummy Response" // 결과값 반환
    }

    println(result) // Dummy Response 출력
}

/**
	결과
    Dummy Response
*/
```

async-await 쌍을 withContext 함수로 대체하는 방법입니다.

### withContext의 동작 방식

async-await 쌍은 새로운 코루틴을 생성해 작업을 처리하지만 withContext 함수는 실행 중이던 코루틴을 그대로 유지시킨 채로 코루틴의 실행 환경만 변경해 작업을 처리한다.

```kotlin
fun main() = runBlocking<Unit> {
    println("[${Thread.currentThread().name}] runBlocking 블록 실행")
    withContext(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] withContext 블록 실행")
    }
}
/**
  결과
  [main @coroutine#1] runBlocking 블록 실행
  [DefaultDispatcher-worker-1 @coroutine#1] withContext 블록 실행
*/
```

코드의 실행 결과를 보면 runBlocking 함수의 block 람다식을 실행하는 스레드와 withContext 함수의 block 람다식을 실행하는 스레드는 main과 DefaultDispathcer-worker-1으로 다르지만 코루틴은 coroutine#1으로 같은것을 볼 수 있습니다.

**즉 withContext는 새로운 코루틴을 만들지 않고 기존 코루틴에서 CoroutineContext 객체만 바꿔서 실행됩니다.**

withContext 함수의 동작 방식을 좀 더 자세히 알아보자.
withContext 함수가 호출되면 실행중인 코루틴의 실행 환경이 withContext 함수의 context 인자 값으로 변경돼 실행되며, 이를 컨텍스트 스위칭이라고 부릅니다.
만약 context 인자로 CoroutineDispatcher 객체가 넘어온다면 코루틴은 해당 Coroutine Dispatcher 객체를 사용해 다시 실행됩니다.

따라서 앞의 코드에서 withContext(Dispatchers.IO)가 호출되면 해당 코루틴은 다시 Dispatchers.IO의 작업 대기열로 이동한 후 Dispatchers.IO가 사용할 수 있는 스레드 중 하나로 보내져 실행됩니다.

>**withContext 호출 시 주의점**
withContext 함수는 새로운 코루틴을 만들지 않기 때문에 하나의 코루틴에서 withContext 함수가 여러 번 호출되면 순차적으로 실행된다. 즉 복수의 독립적인 작업이 병렬로 실행돼야 하는 상황에서 withContext를 사용할 경우 성능에 문제를 일으킬 수 있다.



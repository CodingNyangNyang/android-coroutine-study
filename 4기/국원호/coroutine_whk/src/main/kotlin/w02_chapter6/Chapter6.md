> '정석준님 (https://github.com/sjjeong)'께서 주관하는 Kotlin Coroutine 스터디 진행 과정
**코틀린 코루틴의 정석** 책을 읽고 이해한 내용을 바탕으로 정리하여 글을 작성하였습니다. 부족한 부분이나, 틀린 부분이 있다면 반영할 수 있도록 하겠습니다.

## 6장 - CoroutineContext
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

대표적인 코루틴 빌더 함수인 launch와 async를 살펴보면 매개변수로 아래와 같이 가지고 있습니다.
- **context**: CoroutineContext
- **start**: CoroutineStart
- **block**: Unit or 제네릭 타입 T

이번 6장에서 알아볼 것은 **CoroutineContext** 입니다.

### CoroutineContext 란?
CoroutineContext는 코루틴을 실행하는 실행 환경을 설정하고 관리하는 인터페이스로 CoroutineContext 객체는 CoroutineDispatcher, CoroutineName, Job 등의 객체를 조합해 코루틴의 실행 환경을 설정합니다.

즉 CoroutineContext 객체는 코루틴을 실행하고 관리하는 데 핵심적인 역할을 하며, 코루틴의 실행과 관련된 모든 설정은 CoroutineContext 객체를 통해 이루워집니다.

### CoroutineContext 의 구성 요소
CoroutineContext 객체는 CoroutineName, CoroutineDispatcher, Job, CoroutineExceptionHandler의 네 가지 구성 요소를 가집니다.

- **CoroutineName**: 코루틴의 이름을 설정한다.
- **CoroutineDispatcher**: 코루틴을 스레드에 할당해 실행한다.
- **Job**: 코루틴의 추상체로 코루틴을 조작하는 데 사용된다.
- **CoroutineExceptionHandler**: 코루틴에서 발생한 예외를 처리한다.

### CoroutineContext가 구성 요소를 관리하는 방법
| 키 | 값 |
| :---: | :---: |
| CoroutineName 키 | CoroutineName 객체 |
| CoroutineDispatcher 키 | CoroutineDispatcher 객체 |
| Job 키 | Job 객체 |
| CoroutineExceptionHandler 키 | CoroutineExceptionHandler 객체 |

CoroutineContext 객체는 키-값 쌍으로 각 구성 요소를 관리합니다.
각 구성 요서는 고유한 키를 가지며, 키에 대해 중복된 값은 허용되지 않습니다.
따라서 CorotuineContext 객체는 구성 요소인 CoroutineName, CoroutineDispatcher, Jobm CoroutineExceptionHandler 객체를 한 개씩만 가질 수 있습니다.

### CoroutineContext 구성
CoroutineContext 객체에 구성 요소를 추가하는 방법으로는 CorotineContext 객체 간에 **더하기 연산자(+)**를 사용할 수 있습니다.

```kotlin
fun main() = runBlocking<Unit> {
    val coroutineContext: CoroutineContext = newSingleThreadContext("EnsoThread") + CoroutineName("EnsoCoroutine")

    launch(context = coroutineContext) {
        println("[${Thread.currentThread().name}] 실행")
    }
}

/** 
	결과
    [EnsoThread @EnsoCoroutine#2] 실행
*/
```

| 키 | 값 |
| :---: | :---: |
| CoroutineName 키 | CoroutineName("EnsoCoroutine") |
| CoroutineDispatcher 키 | newSingleThreadContext("EnsoThread") |
| Job 키 | 설정되지 않음 |
| CoroutineExceptionHandler 키 | 설정되지 않음 |

구성 요소가 없는 CoroutineContext는 EmptyCoroutineContext를 통해 만들 수 있습니다.

```kotlin
val emptyCoroutineContext: CoroutineContext = EmptyCoroutineContext
```

### CoroutineContext 구성 요소 덮어씌우기
만약 CoroutineContext 객체에 같은 구성 요소가 둘 이상 더해진다면 **나중에 추가된 CoroutineContext 구성 요소가 이전의 값을 덮어 씌웁니다.**

```kotlin
fun main() = runBlocking<Unit> {
    val coroutineContext: CoroutineContext = newSingleThreadContext("EnsoThread") + CoroutineName("EnsoCoroutine")
    val newCoroutineContext: CoroutineContext = coroutineContext + CoroutineName("NewCoroutine")

    launch(context = newCoroutineContext) {
        println("[${Thread.currentThread().name}] 실행")
    }

}
/**
	결과
    [EnsoThread @NewCoroutine#2] 실행
*/
```

위 코드를 본다면 아래와 같은 결과 값이 나왔습니다.


| coroutineContext |
| :---: |
| CoroutineName("EnsoCoroutine") |
| newSingleThreadContext("EnsoThread") |

---
_덮어씌우기 (+)_

| add coroutineContext |
| :---: |
| CoroutineName("NewCoroutine") |

---
**결과**

| newCoroutineContext |
| :---: |
| CoroutineName("NewCoroutine") |
| newSingleThreadContext("EnsoThread") |


위 표와 같이 나중에 추가된 CoroutineName("NewCoroutine") 이 결과에 표시 되는것을 볼 수 있습니다.


### CoroutineContext에 Job 생성해 추가하기
Job 객체는 기본적으로 launch나 runBlocking 같은 코루틴 빌더 함수를 통해 자동으로 생성되지만 Job()을 호출해서 생성할 수도 있습니다.

```kotlin
val myJob = Job()
val coroutineContext: CoroutineContext = Dispatchers.IO + myJob
```
이를 사용해서 위와 같이 CoroutineContext에 Job 객체를 추가할 수 있습니다.

| 키 | 값 |
| :---: | :---: |
| CoroutineName 키 | 설정되지 않음 |
| CoroutineDispatcher 키 | Dispatchers.IO |
| Job 키 | myJob |
| CoroutineExceptionHandler 키 | 설정되지 않음 |

### CoroutineContext 구성 요소에 접근하기
CoroutineContext 객체의 각 구성 요소에 접근할 수 있는 고유한 키가 있습니다.

이 Key는 일반적으로 CoroutineContext 자신의 내부에 CoroutineContext.Key 인터페이스를 구현한 싱글톤 객체로 구현됩니다.

```kotlin
public interface Job : CoroutineContext.Element {
    public companion object Key : CoroutineContext.Key<Job>
    ...
}

public data class CoroutineName(
) : AbstractCoroutineContextElement(CoroutineName) {
    public companion object Key : CoroutineContext.Key<CoroutineName>
    ...
}
```

여기에 의문이 든점은 싱글톤으로 구현이 되어있다면 각 구성요소 타입들의 Key는 동일할까? 코드를 통해서 확인 해보겠습니다.
```kotlin
@OptIn(ExperimentalStdlibApi::class)
fun main() = runBlocking<Unit> {
    val nameKey1 = CoroutineName("EnsoCoroutine").key
    val nameKey2 = CoroutineName("NewCoroutine").key

    val dispatcherKey1 = Dispatchers.IO.key
    val dispatcherKey2 = Dispatchers.Main.key
    val dispatcherKey3 = newSingleThreadContext("MyThread").key

    println("name과 dispatcher 키 동일 : ${nameKey1 == dispatcherKey1}")
    println("CoroutineName 객체 간 키 동일 :  ${nameKey1 == nameKey2}")
    println("CoroutineName 키 동일 :  ${nameKey1 == CoroutineName.Key}")
    println("Dispatchers 객체 간 키 동일 1 :  ${dispatcherKey1 == dispatcherKey2}")
    println("Dispatchers 객체 간 키 동일 2 :  ${dispatcherKey1 == dispatcherKey3}")
    println("Dispatchers 키 동일 :  ${dispatcherKey1 == CoroutineDispatcher.Key}")
}

/**
	결과
    name과 dispatcher 키 동일 : false
	CoroutineName 객체 간 키 동일 :  true
	CoroutineName 키 동일 :  true
	Dispatchers 객체 간 키 동일 1 :  true
	Dispatchers 객체 간 키 동일 2 :  true
	Dispatchers 키 동일 :  false
*/
```

각 구성 요소 타입에 대해서는 동일한것으로 확인할 수 있었습니다.
그렇지만 CoroutineName.Key 는 동일하지 않은것으로 나오네요.

#### 키를 사용해 CoroutineContext 구성 요소에 접근하기

```kotlin
fun main() = runBlocking<Unit> {
  val coroutineContext = CoroutineName("MyCoroutine") + Dispatchers.IO
  val nameFromContext = coroutineContext[CoroutineName.Key]
  println(nameFromContext)
}

/**
	결과
	CoroutineName(MyCoroutine)
*/
```

#### 구성 요소 자체를 키로 사용해 CoroutineContext 구성 요소에 접근하기
```kotlin
fun main() = runBlocking<Unit> {
  val coroutineContext = CoroutineName("MyCoroutine") + Dispatchers.IO
  val nameFromContext = coroutineContext[CoroutineName] // '.Key'제거
  println(nameFromContext)
}

/**
	결과
	CoroutineName(MyCoroutine)
*/
```

#### 구성 요소의 key 프로퍼티를 사용해 CoroutineContext 구성 요소에 접근하기
```kotlin
fun main() = runBlocking<Unit> {
  val coroutineName : CoroutineName = CoroutineName("MyCoroutine")
  val dispatcher : CoroutineDispatcher = Dispatchers.IO
  val coroutineContext = coroutineName + dispatcher

  println(coroutineContext[coroutineName.key]) // CoroutineName("MyCoroutine")
  println(coroutineContext[dispatcher.key]) // Dispatchers.IO
}

/**
	결과
  	CoroutineName(MyCoroutine)
  	Dispatchers.IO
*/
```

### CoroutineContext 구성 요소 제거하기
CoroutineContext 객체는 구성 요소를 제거하기 위한 minusKey 함수를 제공합니다.
mynusKey 함수는 구성 요소의 키를 인자로 받아 해당 구성 요소를 제거한 CoroutineContext 객체를 반환합니다.

```kotlin
fun main() = runBlocking<Unit> {
  val coroutineName = CoroutineName("MyCoroutine")
  val dispatcher = Dispatchers.IO
  val myJob = Job()
  val coroutineContext: CoroutineContext = coroutineName + dispatcher + myJob

  val deletedCoroutineContext = coroutineContext.minusKey(CoroutineName)

  println(deletedCoroutineContext[CoroutineName])
  println(deletedCoroutineContext[CoroutineDispatcher])
  println(deletedCoroutineContext[Job])
}

/**
	결과
	null
	Dispatchers.IO
	JobImpl{Active}@65e2dbf3
*/
```

> **minusKey 함수 사용 시 주의할 점**
minusKey 함수 사용 시 주의할 점은 minusKey를 호출한 CoroutineContext 객체는 그대로 유지되고, 구성 요소가 제거된 새로운 CoroutineContext 객체가 반환 된다는 것 입니다.
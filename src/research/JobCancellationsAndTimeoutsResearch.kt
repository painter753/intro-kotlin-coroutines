package research

import kotlinx.coroutines.*
import java.lang.Exception

class JobCancellationsAndTimeoutsResearch {
}

fun main(): Unit = runBlocking {
    val result = jobWithTimeoutOrNull()
    println("Result $result")
}

suspend fun jobWithTimeoutOrNull() =
    withTimeoutOrNull(1300L) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
        "Done"
    }

suspend fun jobWithTimeout() =
    withTimeout(1300L) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }

suspend fun cancellableJob() = coroutineScope {
    var job = launch {
        println("Start big job")
        repeat(10) {
            delay(1000)
            println("I's sleeping in $it")
        }
        println("Completed")
    }

    delay(3000)
    //job.cancel()
    println("Job has been cancelled")
    //job.join()
}

suspend fun impossibleToCancel() = coroutineScope {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (i < 5) { // computation loop, just wastes CPU
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("job: I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
            some()
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

suspend fun tryToCancelWithYield() = coroutineScope {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (i < 5) { // computation loop, just wastes CPU
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("job: I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
            yield()
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

suspend fun tryToCancelWithIsActiveFlag() = coroutineScope {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) { // computation loop, just wastes CPU
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("job: I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

suspend fun useTryFinallyBlock() = coroutineScope {
    val job = launch {
        try {
            repeat(1000) {i ->
                println("job: I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            withContext(NonCancellable) {
                println("job: I'm running finally")
                //delay(1000)
                some()
                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
            }

        }
    }

    delay(1300L)
    println("I'm tired of waiting!")
    job.cancelAndJoin()
    println("main: Now I can quit")
}

suspend fun useTryFinallyBlockToShowCancellationException() = coroutineScope {
    val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                try {
                    println("job: I'm running finally")
                    //delay(1000)
                    some()
                    println("job: And I've just delayed for 1 sec because I'm non-cancellable")
                } catch (e: Exception) {
                    when (e) {
                        is CancellationException -> {
                            println("Gotcha! ${e}")
                            throw e
                        }
                        else -> println("Another exception")
                    }
                }
            }
    }

    delay(1300L)
    println("I'm tired of waiting!")
    job.cancelAndJoin()
    println("main: Now I can quit")
}


suspend fun some() {
    val a = 1 + 3
    delay(1)
}
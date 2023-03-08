package tasks

import contributors.MockGithubService
import contributors.expectedResults
import contributors.testRequestData
import kotlinx.coroutines.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class Request4SuspendKtTest {
    @Test
    fun testSuspend() = runTest {
        val startTime = currentTime
        val result = loadContributorsSuspend(MockGithubService, testRequestData)
        Assert.assertEquals("Wrong result for 'loadContributorsSuspend'", expectedResults.users, result)
        val totalTime = currentTime - startTime

        Assert.assertEquals(
            "The calls run consequently, so the total virtual time should be 4000 ms: " +
                    "1000 for repos request plus (1000 + 1200 + 800) = 3000 for sequential contributors requests)",
            expectedResults.timeFromStart, totalTime
        )

        Assert.assertTrue(
            "The calls run consequently, so the total time should be around 4000 ms: " +
                    "1000 for repos request plus (1000 + 1200 + 800) = 3000 for sequential contributors requests)",
            totalTime in expectedResults.timeFromStart..(expectedResults.timeFromStart + 500)
        )
    }

    @Test
    fun testDelayInSuspend() = runTest {
        val realStartTime = System.currentTimeMillis()
        val virtualStartTime = currentTime

        foo()
        println("${System.currentTimeMillis() - realStartTime} ms") // ~ 6 ms
        println("${currentTime - virtualStartTime} ms")             // 1000 ms
    }

    suspend fun foo() {
        delay(1000)    // auto-advances without delay
        println("foo") // executes eagerly when foo() is called
    }

    @Test
    fun testDelayInLaunch() = runTest {
        val realStartTime = System.currentTimeMillis()
        val virtualStartTime = currentTime

        bar()

        println("${System.currentTimeMillis() - realStartTime} ms") // ~ 11 ms
        println("${currentTime - virtualStartTime} ms")             // 1000 ms
    }

    suspend fun bar() = coroutineScope {
        launch {
            delay(1000)    // auto-advances without delay
            println("bar") // executes eagerly when bar() is called
        }
    }
}
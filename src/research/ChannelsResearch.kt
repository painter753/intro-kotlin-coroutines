package research

import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.repackaged.net.bytebuddy.utility.RandomString
import kotlinx.coroutines.runBlocking

class ChannelsResearch {
}


fun main(): Unit = runBlocking {
    async { Sender().start() }

    delay(5000L)

    async { Receiver().start() }


}


class Sender() {
    val output: SendChannel<String> = channel

    suspend fun start() {
        repeat(10) {
            delay(1000L)
            val str = RandomString(5).nextString()
            println(str)
            output.send(str)
        }.also {
            channel.close()
        }
    }
}

val channel = Channel<String>(5)

class Receiver() {
    val input: ReceiveChannel<String> = channel

    suspend fun start() {

        for (str in channel) {
            println("Receive: $str")
        }
    }
}
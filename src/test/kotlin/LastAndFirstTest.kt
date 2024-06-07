import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class LastAndFirstTest {
    @Test
    fun `test flow with last operator`() {
        val flow1 =
            MutableSharedFlow<Int?>(replay = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 10)

        runTest {
            launch {
                println(flow1.last())
            }

            launch {
                flow1.emit(null)
                flow1.emit(1)
                delay(100)
                flow1.emit(2)
                delay(200)
                flow1.emit(3)
                delay(100)
                flow1.emit(4)
                flow1.emit(null)
            }
        }
    }

    @Test
    fun `test flow with first operator`() {
        val flow1 =
            MutableSharedFlow<Int?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

        runTest {
            launch {
                println(flow1.firstOrNull())
            }

            launch {
                flow1.emit(null)
                delay(10)
                flow1.emit(1)
                delay(10)
                flow1.emit(2)
                flow1.emit(3)
                flow1.emit(4)
            }
        }
    }
}
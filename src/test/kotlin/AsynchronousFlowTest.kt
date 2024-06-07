import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class AsynchronousFlowTest {

    val sharedFlow: MutableSharedFlow<Int> = MutableSharedFlow(
        extraBufferCapacity = 10,
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    var sharedFlow2: MutableSharedFlow<String> = MutableSharedFlow(
        extraBufferCapacity = 10,
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    var counter = 0

    @Test
    fun `Asynchronous shared flow test`() {
        runTest {
            repeat(3) {
                launch {
                    for (counter in 1..10) {
                        sharedFlow.emit(counter)
                    }
                }
            }

            launch {
                sharedFlow.collect {
                    println(it)
                }
            }
        }
    }

    @Test
    fun `Asynchronous sharedFlow with combine`() {
        runTest {
            launch {
                sharedFlow.combine(sharedFlow2) { a, b -> "$a $b" }.collect {
                    println(it)
                }
            }
            launch {
                sharedFlow.emit(1)
                sharedFlow.emit(4)

                sharedFlow2.emit("a")
                sharedFlow2.emit("t")
            }
            delay(2000)


            sharedFlow.emit(2)
            sharedFlow2.emit("b")
            sharedFlow.emit(3)
            sharedFlow2.emit("c")
        }
    }

    @Test
    fun `Asynchronous stateFlow test`() {
        val viewState = MutableStateFlow<ExampleViewState>(ExampleViewState(null, null))
        val stringSharedFlow = MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        val intSharedFlow = MutableSharedFlow<Int>(replay = 1, extraBufferCapacity = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)

        runTest {
            launch {
                stringSharedFlow.collect { stringValue ->
                    //println(stringValue)
                    delay(10)
                    viewState.update {
                        it.copy(string = stringValue)
                    }
                }
            }

            launch {
                intSharedFlow.collect { intValue ->
                    //  println(intValue)
                    delay(10)
                    viewState.update {
                        it.copy(int = intValue)
                    }
                }
            }

            launch {
                viewState.collect {
                    println(it)
                }
            }

            launch {
                stringSharedFlow.emit("b")
                stringSharedFlow.emit("w")
                stringSharedFlow.emit("t")
            }

            launch {
                intSharedFlow.emit(3)
                intSharedFlow.emit(2)
                intSharedFlow.emit(1)
            }
        }
    }

    @Test
    fun `Asynchronous stateFlow test2`() {
        val viewState = MutableStateFlow<ExampleViewState>(ExampleViewState(null, null))

        runTest {
            launch {
                viewState.collect {
                    println(it)
                }
            }

            launch {
                viewState.update {
                    it.copy(int = 1)
                }
            }

            launch {
                delay(10)
                viewState.update {
                    it.copy(string = "a", int = 2)
                }
            }
        }
    }

    data class ExampleViewState(
        val string: String?,
        val int: Int?
    )
}
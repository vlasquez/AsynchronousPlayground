import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class MutexTest {

    var concurrentData = ConcurrentData()

    val mutex = Mutex()

    @Test
     fun `Mutex behavior test`() {
         runTest {
            val name = launch {
                 changeName("John")
                 println(concurrentData)
                 println("name")
             }

            val id = launch {

                changeId(1)
                 println(concurrentData)
                 println("id")
             }

            val phone = launch {

                addPhone("123456789")
                 println(concurrentData)
                 println("phone")
             }

            val phone2 = launch {

                addPhone("222333443")
                 println(concurrentData)
                 println("phone")
             }
            val phone3 = launch {
                addPhone("999888787")
                 println(concurrentData)
                 println("phone")
             }

             joinAll(id, name, phone, phone2, phone3)
         }
     }

    @Test
    fun `Mutex behavior test2`() {

        val mutex = Mutex()
        var counter = 0

        runTest {
            val jobs = List(100) {
                launch {
                    repeat(1000) {
                        mutex.withLock {
                            counter++
                        }
                    }
                }
            }
            jobs.forEach { it.join() }
            println("Counter: $counter")
        }
    }

    private suspend fun changeName(name: String) {
        mutex.withLock {
            concurrentData =  concurrentData.copy(name = name)
        }
    }

    private suspend fun changeId(id: Int) {
        mutex.withLock {
           concurrentData = concurrentData.copy(id = id)
        }
    }

    private suspend fun addPhone(phone: String) {
        mutex.withLock {
            concurrentData =  concurrentData.copy(
                phoneList =
                if (concurrentData.phoneList.isNullOrEmpty())
                    listOf(phone)
                else
                    concurrentData.phoneList?.plus(phone)
            )
        }
    }

    data class ConcurrentData(
        val id: Int? = null,
        val name: String? = null,
        val phoneList: List<String>? = null
    )
}
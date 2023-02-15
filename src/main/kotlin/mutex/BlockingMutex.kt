package mutex

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun <T> Mutex.withLockBlocking(action: () -> T): T {
    return runBlocking {
        withLock(action = action)
    }
}
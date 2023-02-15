package mutex

import kotlinx.coroutines.sync.Mutex
import java.lang.ref.WeakReference

class HashMutex<K> {
    private val map = mutableMapOf<K, WeakReference<Mutex>>()

    operator fun get(key: K): Mutex {
        val mutex = map[key]?.get() ?: Mutex().apply {
            map[key] = WeakReference(this)
        }

        return mutex
    }
}
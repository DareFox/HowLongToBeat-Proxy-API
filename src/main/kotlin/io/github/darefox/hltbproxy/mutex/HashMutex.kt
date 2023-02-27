package io.github.darefox.hltbproxy.mutex

import kotlinx.coroutines.sync.Mutex
import org.apache.commons.collections4.map.AbstractReferenceMap
import org.apache.commons.collections4.map.ReferenceMap
import java.lang.ref.WeakReference

class HashMutex<K : Any> {
    private val map = ReferenceMap<K, Mutex>(
        AbstractReferenceMap.ReferenceStrength.HARD,
        AbstractReferenceMap.ReferenceStrength.WEAK
    )

    operator fun get(key: K): Mutex {
        val mutex = synchronized(key) {
            map[key] ?: Mutex().apply {
                map[key] = this
            }
        }

        return mutex
    }
}
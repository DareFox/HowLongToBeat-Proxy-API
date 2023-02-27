package io.github.darefox.hltbproxy.cache

import io.github.darefox.hltbproxy.mutex.HashMutex
import io.github.darefox.hltbproxy.mutex.withLockBlocking

/**
 * Get element from cache by string key or create it via generator, save it and return it
 */
fun <V> Cache<String, V>.getOrGenerateBlocking(mutexMap: HashMutex<String>, key: String, generator: () -> V): V {
    val cached = this[key]
    if (cached != null) return cached

    return mutexMap[key + "_mutex"].withLockBlocking {
        val cached = this[key]
        cached ?: generator().also {
            this[key] = it
        }
    }
}
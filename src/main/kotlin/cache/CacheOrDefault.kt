package cache

import jsonKotlinx
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import mutex.HashMutex
import mutex.withLockBlocking

fun <K,V> Cache<K,V>.getOrGenerateBlocking(mutexMap: HashMutex<K>, key: K, generator: () -> V): V {
    val cached = this[key]
    if (cached != null) return cached

    return mutexMap[key].withLockBlocking {
        val cached = this[key]
        cached ?: generator().also {
            this[key] = it
        }
    }
}

inline fun <K, reified V> Cache<K,JsonElement>.getOrGenerateBlockingJson(mutexMap: HashMutex<K>, key: K, crossinline generator: () -> V): JsonElement {
    val cached = this[key]
    if (cached != null) return cached

    return mutexMap[key].withLockBlocking {
        val cached = this[key]
        cached ?: jsonKotlinx.encodeToJsonElement(generator())
    }
}
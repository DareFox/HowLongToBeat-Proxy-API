package io.github.darefox.hltbproxy.cache

import io.github.darefox.hltbproxy.jsonKotlinx
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import io.github.darefox.hltbproxy.mutex.HashMutex
import io.github.darefox.hltbproxy.mutex.withLockBlocking

/**
 * Get element from cache or create it via generator, save it and return it
 */
fun <K,V> Cache<K, V>.getOrGenerateBlocking(mutexMap: HashMutex<K>, key: K, generator: () -> V): V {
    val cached = this[key]
    if (cached != null) return cached

    return mutexMap[key].withLockBlocking {
        val cached = this[key]
        cached ?: generator().also {
            this[key] = it
        }
    }
}

/**
 * Get JsonElement from cache or create it via generator, convert it to JsonElement, save it and return it
 */
inline fun <K, reified V> Cache<K, JsonElement>.getOrGenerateBlockingJson(mutexMap: HashMutex<K>, key: K, crossinline generator: () -> V): JsonElement {
    val cached = this[key]
    if (cached != null) return cached

    return mutexMap[key].withLockBlocking {
        val cached = this[key]
        cached ?: jsonKotlinx.encodeToJsonElement(generator())
    }
}
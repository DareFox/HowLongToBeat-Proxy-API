package io.github.darefox.hltbproxy.cache

import java.lang.ref.WeakReference
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class WeakExpiringLRUCache<K, V>(val maxSize: Int = 1_000_000, val lifetime: Duration) : Cache<K, V> {
    init {
        require(maxSize > 0) {
            "Max size of cache can't be 0 or less"
        }
    }

    private val map = WeakHashMap<K, WeakReference<CacheEntry<V>>>()

    override val size: Int
        get() = map.size

    override fun clear() {
        map.clear()
    }

    override fun remove(key: K): V? {
        return map.remove(key)?.get()?.value
    }

    override fun get(key: K): V? {
        val entry = map[key]
        return entry?.get()?.checkLifetime(key)?.access(key)
    }

    private fun CacheEntry<V>.checkLifetime(key: K): CacheEntry<V>? {
        val currentNano = System.nanoTime()

        val livedFor = (currentNano - createdAtNano).toDuration(DurationUnit.NANOSECONDS)
        return if (livedFor > lifetime) {
            remove(key)
            null
        } else {
            this
        }
    }

    private fun CacheEntry<V>.access(key: K): V? {
        val current = System.nanoTime()
        map[key] = WeakReference(this.copy(
            accessedAtNano = current
        ))
        return value
    }

    override fun set(key: K, value: V) {
        removeLastUsedUntilNotFull()
        val current = System.nanoTime()
        map[key] = WeakReference(
            CacheEntry(
                createdAtNano = current,
                accessedAtNano = Long.MIN_VALUE,
                value = value
            )
        )
    }

    private fun isOverflowedOrFull(): Boolean {
        return size >= maxSize
    }

    private fun removeLastUsedUntilNotFull() {
        while (isOverflowedOrFull()) {
            val leastUsed = map.entries.sortedBy {
                it.value.get()?.accessedAtNano
            }.first()

            remove(leastUsed.key)
        }
    }
}

private data class CacheEntry<V>(val createdAtNano: Long, val accessedAtNano: Long, val value: V)
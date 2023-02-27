package io.github.darefox.hltbproxy.map

import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private typealias Table<K,V> = Array<CollectableHashMapEntry<K,V>?>

/**
 * LinkedHashMap, but it has weak key-value pairs that can be collected by GC at any time
 */
class CollectableHashMap<K,V>: MutableMap<K,V> {
    private val lock = ReentrantLock()
    private val threshold = 0.75;
    private var table: Table<K,V> = arrayOfNulls(16)
    private val atomicCounter = AtomicInteger(0)

    private fun calculateHash(obj: Any?): Int {
        if (obj == null)
            return 0

        val objHash = obj.hashCode()

        return objHash xor (objHash ushr 16)
    }
    private fun calculateIndex(hash: Int): Int {
        return hash and (table.size - 1)
    }

    private fun putTo(key: K, value: V, table: Table<K,V>): SearchResult<K,V>? {
        val hash = calculateHash(key)
        val index = calculateIndex(hash)
        val newPair = KeyValuePair(
            hash, key, value
        )

        val start = table[index]
        val previousPair = start?.pair?.get()
        val next = start?.cleanNext()

        // If first element is empty or collected
        if (start == null || start.isCollected) {
            table[index] = CollectableHashMapEntry(WeakReference(newPair), null)
            return null
        }

        // If first element has same key and hash
        if (previousPair?.isSameAs(key, hash) == true) {
            table[index] = CollectableHashMapEntry(WeakReference(newPair), null)
            return SearchResult<K,V>(start, start, previousPair)
        }

        // If element is only one and is not equal, then create new
        if (next == null) {
            start.next = CollectableHashMapEntry(WeakReference(newPair), null)
            return null
        }

        // Else check all next elements
        lateinit var last: CollectableHashMapEntry<K,V>
        var lastPair: KeyValuePair<K,V>? = null
        val result = next.firstOrNull {
            last = it
            lastPair = it.pair.get()

            lastPair?.isSameAs(key, hash) ?: false
        }

        // If no result, then add to last element
        return if (result == null) {
            last.next = CollectableHashMapEntry(WeakReference(newPair), null)
            null
        } else {
            last.next = CollectableHashMapEntry(WeakReference(newPair), result.cleanNext())
            SearchResult(
                last,
                last,
                lastPair
            )
        }
    }
    private fun getFrom(key: K, table: Table<K, V>): SearchResult<K,V>? {
        val hash = calculateHash(key)
        val index = calculateIndex(hash)

        var lastPair: KeyValuePair<K,V>? = null
        var last: CollectableHashMapEntry<K,V>? = null
        val start = table[index] ?: return null

        // clean collected entry
        if (start.isCollected) {
            table[index] = start.cleanNext()
        }

        val result = start.firstOrNull {
            last = it
            lastPair = it.pair.get()
            lastPair?.isSameAs(key, hash) == true
        }

        return if (last != null) {
            SearchResult(
                last!!, result, lastPair
            )
        } else {
            null
        }
    }

    private fun KeyValuePair<K,V>.isSameAs(key: K, hash: Int): Boolean {
        return this.hash == hash && this.key == key
    }

    private fun checkAndResize() {
        val isOverThreshold = atomicCounter.get() > (threshold * table.size)
        val newSize = table.size * 2
        val cappedNewSize = if (newSize > Int.MAX_VALUE) {
            Int.MAX_VALUE
        } else {
            newSize
        }.toInt()

        if (isOverThreshold) {
            resize(cappedNewSize)
        }
    }

    private fun resize(newSize: Int) {
        if (newSize == table.size)
            return

        val new: Table<K,V> = arrayOfNulls(newSize)
        entries.forEach {
            putTo(it.key, it.value, new)
        }

        table = new
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = locked {
            table.mapNotNull {
                it?.mapNotNull { entry ->
                   val pair = entry.pair.get() ?: return@mapNotNull null

                    CollectableMutableEntry(pair.key, pair.value)
                }
            }.flatten().toMutableSet()
        }
    override val keys: MutableSet<K>
        get() = locked {
            table.mapNotNull { start ->
                val list = mutableListOf<K>()
                start?.forEach { it  ->
                    val pair = it.pair.get()
                    if (pair != null) {
                        list += pair.key
                    }
                }
                list
            }.flatten().toMutableSet()
        }
    override val size: Int
        get() = locked {
            table.sumOf {
                it?.sumOf {
                    if (it?.isCollected == true) {
                        0 as Int
                    } else {
                        1 as Int
                    }
                } ?: 0
            }
        }
    override val values: MutableCollection<V>
        get() = locked {
            table.mapNotNull {
                val list = mutableListOf<V>()
                it?.forEach {
                    val pair = it.pair.get()
                    if (pair != null) {
                        list += pair.value
                    }
                }
                list
            }.flatten().toMutableList()
        }

    private fun <T> locked(action: () -> T): T {
        return action() // lock.withLock(action)
    }

    override fun clear() = locked {
        table = arrayOfNulls(16)
    }

    override fun isEmpty(): Boolean = locked {
        val isNotEmpty = table.any {
            it?.any {
                !it.isCollected
            } ?: false
        }

        !isNotEmpty
    }

    override fun remove(key: K): V? = locked {
        val search = getFrom(key, table)

        if (search?.result == null) {
            return@locked null
        }

        search.last.next = search.result.cleanNext()
        atomicCounter.decrementAndGet()
        search.resultKeyPair!!.value
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (key, value) ->
            putTo(key, value, table).also {
                if (it?.resultKeyPair == null) {
                    atomicCounter.incrementAndGet()
                }
            }
            checkAndResize()
        }
        println()
    }

    override fun put(key: K, value: V): V? = locked {
        putTo(key, value, table).also {
            if (it?.resultKeyPair == null) {
                atomicCounter.incrementAndGet()
            }

            checkAndResize()
        }?.resultKeyPair?.value
    }

    override fun get(key: K): V? = locked {
        getFrom(key, table)?.result?.pair?.get()?.value
    }

    override fun containsValue(value: V): Boolean  = locked {
        table.any { start ->
            start?.any {
                it.pair.get()?.value == value
            } ?: false
        }
    }
    override fun containsKey(key: K): Boolean = locked {
        val hash = calculateHash(key)
        val index = calculateIndex(hash)
        table[index]?.any {
            it.pair.get()?.isSameAs(key, hash) == true
        } ?: false
    }

}


private data class SearchResult<K,V>(
    /**
     * Last accessed in chain entry
     */
    val last: CollectableHashMapEntry<K,V>,
    /**
     * Result of searching by hash+index+key
     */
    val result: CollectableHashMapEntry<K,V>?,
    /**
     * Key-value pair of result
     */
    val resultKeyPair: KeyValuePair<K,V>?
)
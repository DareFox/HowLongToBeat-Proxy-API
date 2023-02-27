package io.github.darefox.hltbproxy.map

internal class CollectableHashMapEntryIterator<K,V>(
    private var current: CollectableHashMapEntry<K, V>
): Iterator<CollectableHashMapEntry<K, V>> {
    private var next: CollectableHashMapEntry<K, V>? = current
    override fun hasNext(): Boolean {
        return next != null
    }

    override fun next(): CollectableHashMapEntry<K, V> {
        return next!!.also {
            next = it.next
        }
    }
}
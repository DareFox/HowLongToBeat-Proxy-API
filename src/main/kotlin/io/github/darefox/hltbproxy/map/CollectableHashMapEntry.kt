package io.github.darefox.hltbproxy.map

import java.lang.ref.WeakReference

internal class CollectableHashMapEntry<K,V>(
    var pair: WeakReference<KeyValuePair<K, V>>,
    var next: CollectableHashMapEntry<K, V>?
): Iterable<CollectableHashMapEntry<K, V>> {
    fun cleanNext(): CollectableHashMapEntry<K,V>? {
        if(next?.isCollected == true) {
            return next?.firstOrNull() {
                !it.isCollected
            }
        } else {
            return next
        }
    }

    val isCollected
        get() = pair.get() == null

    override fun iterator(): Iterator<CollectableHashMapEntry<K, V>> {
        return CollectableHashMapEntryIterator(this)
    }
}
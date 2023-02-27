package io.github.darefox.hltbproxy.map

class CollectableMutableEntry<K,V>(
    key: K,
    value: V
): MutableMap.MutableEntry<K,V> {
    override val key: K = key
    private var _value = value
    override val value: V
        get() = _value

    override fun setValue(newValue: V): V {
        val old = value
        _value = newValue
        return old
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CollectableMutableEntry<*, *>

        if (key != other.key) return false
        if (_value != other._value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key?.hashCode() ?: 0
        result = 31 * result + (_value?.hashCode() ?: 0)
        return result
    }
}
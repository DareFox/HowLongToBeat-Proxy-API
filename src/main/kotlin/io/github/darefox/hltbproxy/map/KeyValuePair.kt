package io.github.darefox.hltbproxy.map

internal data class KeyValuePair<K,V>(
    val hash: Int,
    val key: K,
    var value: V,
)
package io.github.darefox.hltbproxy.map

fun <K,V> collectableMutableMap(): MutableMap<K,V> = CollectableHashMap<K,V>()
package io.github.darefox.hltbproxy.proxy

import io.github.darefox.hltbproxy.cache.WeakExpiringLRUCache
import kotlinx.serialization.json.JsonElement
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val cache = WeakExpiringLRUCache<String, JsonElement>(lifetime = 1.toDuration(DurationUnit.HOURS))
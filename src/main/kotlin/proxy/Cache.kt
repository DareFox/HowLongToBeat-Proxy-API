package proxy

import cache.WeakExpiringLRUCache
import kotlinx.serialization.json.JsonElement
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val cache = WeakExpiringLRUCache<String, JsonElement>(lifetime = 1.toDuration(DurationUnit.HOURS))
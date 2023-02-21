package io.github.darefox.hltbproxy.proxy

import io.github.darefox.hltbproxy.cache.WeakExpiringLRUCache
import org.http4k.core.Response
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val cache = WeakExpiringLRUCache<String, Response>(lifetime = 1.toDuration(DurationUnit.HOURS))
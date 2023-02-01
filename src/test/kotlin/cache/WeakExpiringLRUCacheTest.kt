package cache

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Ignore
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.DurationUnit.*
import kotlin.time.toDuration

class WeakExpiringLRUCacheTest {
    fun createCache(limit: Int, lifetime: Duration): WeakExpiringLRUCache<String, String> {
        return WeakExpiringLRUCache<String,String>(maxSize = limit, lifetime = lifetime)
    }


    @BeforeEach
    fun reset() {
        val cache = createCache(Int.MAX_VALUE, Duration.INFINITE)
        cache.clear()
    }

    @Test
    fun getSize() {
        val cache = createCache(Int.MAX_VALUE, Duration.INFINITE)

        cache["test"] = "1"
        assert(cache.size == 1)

        cache["teste"] = "1"
        cache["testee"] = "1"
        cache["testeee"] = "1"
        cache["testeeeee"] = "1"
        assert(cache.size == 5)
    }

    @Test
    fun clear() {
        val cache = createCache(Int.MAX_VALUE, Duration.INFINITE)

        cache["test"] = "1"
        cache.clear()
        assert(cache.size == 0)

        cache.clear()
        assert(cache.size == 0)

    }

    @Test
    fun remove() {
        val cache = createCache(Int.MAX_VALUE, Duration.INFINITE)

        cache["test"] = "1"
        cache["testbr"] = "1"
        cache["testeeee"] = "1"
        assert(cache.size == 3)

        cache.remove("test")
        assert(cache.size == 2)
    }

    @Test
    fun get() {
        val cache = createCache(Int.MAX_VALUE, Duration.INFINITE)

        cache["test"] = "1"
        cache["testbr"] = "2"
        cache["testeeee"] = "3"
        assert(cache["testeeee"] == "3")
    }

    @Test
    fun testLRU() {
        val cache = createCache(10, Duration.INFINITE)

        repeat(20) {
            // $it is 0 indexed
            cache["$it"] = "$it-value"

            if (it % 2 == 0) {
                cache["$it"]
            }
        }

        assertNotNull(cache["2"])
        assertNotNull(cache["4"])
        assertNotNull(cache["6"])
        assertNotNull(cache["8"])
        assertNotNull(cache["10"])
        assertNotNull(cache["12"])
        assertNotNull(cache["14"])
        assertNotNull(cache["16"])
        assertNotNull(cache["18"])
        assertNotNull(cache["19"])
        assert(cache.size == cache.maxSize)
    }

    @Test
    fun testExpire() = runTest {
        val cache = createCache(Int.MAX_VALUE, 2.toDuration(SECONDS))

        val key = "hehe"
        cache.set(key, "")

        Thread.sleep(1000L)
        assertNotNull(cache[key])

        Thread.sleep(1100L)
        assert(cache[key] == null)
    }

    @Test
    fun testWeakness() {
        // create runtime string, collectable by gc
        var key: String? = StringBuilder("keyfr").toString()

        val cache = createCache(Int.MAX_VALUE, Duration.INFINITE)

        cache[key!!] = "test"
        key = null

        System.gc()
        Thread.sleep(4000L)

        assert(cache.size == 0)
    }
}
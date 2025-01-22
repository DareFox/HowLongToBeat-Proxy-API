package io.github.darefox.hltbproxy.proxy

import org.http4k.core.Filter

val corsAll = Filter { next ->
    {
        next(it).header("Access-Control-Allow-Origin", "*")
    }
}
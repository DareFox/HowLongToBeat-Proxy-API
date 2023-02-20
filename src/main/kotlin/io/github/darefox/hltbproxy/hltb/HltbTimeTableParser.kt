package io.github.darefox.hltbproxy.hltb

import org.jsoup.nodes.Element

class HltbTimeTableParser(private val table: Element) {
    init {
        val cssClasses = table.classNames()
        val isTable = table.tag().normalName() == "table"

        require(isTable) {
            "Element isn't <table>"
        }
    }

    private val titleSelector = "thead:nth-child(1) > tr:nth-child(1) > td:nth-child(1)"
    private val body = table.select("tbody").first()!!
    private val columnIndex = table.select(titleSelector).first()!!.siblingElements().associateBy {
        it.siblingIndex() - 1
    }
    val rows = body.children().mapNotNull {
        val title = it.firstElementChild() ?: return@mapNotNull null
        val map = mutableMapOf<String, String>()
        val siblings = title.siblingElements().map {
            val index = it.siblingIndex() - 1
            val column = columnIndex[index]!!
            map[column.ownText()] = it.ownText()
        }

        title.ownText() to map.toMap()
    }.toMap()

    val title = table.select(titleSelector).first()!!.ownText()
}
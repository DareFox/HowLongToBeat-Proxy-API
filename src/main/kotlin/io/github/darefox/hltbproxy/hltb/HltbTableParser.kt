package io.github.darefox.hltbproxy.hltb

import org.jsoup.nodes.Element

class HltbTableParser(private val table: Element) {
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

    fun toSecondsOrNull(convert: String): Long? {
        val hours = "\\d+(?=h|(½|) Hours)".toRegex().find(convert)?.value?.toInt()
        val minutes = "\\d+(?=m)".toRegex().find(convert)?.value?.toInt()
        val seconds = "\\d+(?=s)".toRegex().find(convert)?.value?.toInt()

        val additionalMinutes = if ("½ (Hours|h)".toRegex().find(convert) != null) {
            30L
        } else {
            0L
        }

        if (minutes == null && hours == null && seconds == null && additionalMinutes == 0L) {
            return null
        }

        return ((hours ?: 0) * 60L * 60L) + (((minutes ?: 0) + additionalMinutes) * 60L) + (seconds ?: 0)
    }
}
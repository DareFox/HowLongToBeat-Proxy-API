package io.github.darefox.hltbproxy.hltb

import io.github.darefox.hltbproxy.parse
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat

class HltbOverviewParser(private val html: Document) {
    private val blockDescriptionTitleCssSelector = "div.GameSummary_profile_info__e935c > strong"
    private val dateFormat = listOf(
        SimpleDateFormat("MMMM dd, YYYY"),
        SimpleDateFormat("MMMM YYYY"),
    )

    val title: String by lazy {
        val selector = "div[class*=GameHeader_profile_header_game]"
        val titleSelector = "div[class*=GameHeader_profile_header]"

        html
            .select(selector).first()!!
            .children()
            .select(titleSelector).first()!!
            .ownText()
    }

    val singleplayerTimeTable: HltbSingleplayerTable? by lazy {
        val selector = "table[class*=GameTimeTable_game_main_table]"
        getTable(selector, title = "Single-Player")?.toSingleplayer()
    }
    val multiplayerTimeTable: HltbMultiplayerTable? by lazy {
        val selector = "table[class*=GameTimeTable_game_main_table]"
        getTable(selector, title = "Multi-Player")?.toMultiPlayer()
    }

    val speedrunTimeTable: HltbSpeedrunTable? by lazy {
        val selector = "table[class*=GameTimeTable_game_main_table]"
        getTable(selector, title = "Speedruns")?.toSpeedrun()
    }

    val platformTimeTable: HltbPlatformTable? by lazy {
        val selector = "table[class*=GamePlatformTable_game_main_table]"
        getTable(selector, title = "Platform")?.toPlatform()
    }

    val platforms: List<String>
        get() {
            return getDescByTitleOrNull("Platforms:", "Platform:")?.split(", ")
                ?: listOf()
        }
    val genres: List<String>
        get() {
            return getDescByTitleOrNull("Genres:", "Genre:")?.split(", ")
                ?: listOf()
        }

    val developers: List<String>
        get() {
            return getDescByTitleOrNull("Developer:", "Developers:")?.split(", ")
                ?: listOf()
        }
    val publishers: List<String>
        get() {
            return getDescByTitleOrNull("Publisher:", "Publishers:")?.split(", ")
                ?: listOf()
        }

    val northAmericaRelease: Long?
        get() = getFirstDateByTitleOrNull("NA:")

    val europeRelease: Long?
        get() = getFirstDateByTitleOrNull("EU:")

    val japanRelease: Long?
        get() = getFirstDateByTitleOrNull("JP:")

    val steamId: Long?
        get() {
            var link: String? = null
            html.select("div[class*=GameSummary_profile_info] a").firstOrNull {
                link = it.attr("href")
                link?.contains("https://store.steampowered.com") ?: false
            } ?: return null

            if (link == null) {
                return null
            }

            return "(?<=\\/app\\/)\\d+".toRegex().find(link!!)?.value?.toLongOrNull()
        }

    private fun getTable(selector: String, title: String): HltbTableParser? {
        return html.select(selector).firstOrNull {
            it.tableContainsTitle(title)
        }?.let { HltbTableParser(it) }
    }

    private fun Element.tableContainsTitle(title: String): Boolean {
        val titleParsed = this
            .select("thead:nth-child(1) > tr:nth-child(1) > td:nth-child(1)")
            .firstOrNull()
            ?.ownText() ?: return false
        return this.tag().normalName() == "table" && titleParsed.equals(title, ignoreCase = true)
    }

    private fun getDescByTitleOrNull(vararg titles: String): String? {
        val selector = "div[class*=GameSummary_profile_info]"

        return html.select(selector).firstOrNull {
            val parsedTitle = it.select("strong:nth-child(1)").firstOrNull()?.ownText()
            titles.any { title ->
                parsedTitle.equals(title, ignoreCase = true)
            }
        }?.ownText()
    }

    private fun getFirstDateByTitleOrNull(title: String): Long? {
        val parent = html.select(blockDescriptionTitleCssSelector).firstOrNull {
            it.ownText() == title
        }?.parent() ?: return null

        return dateFormat.parse(parent.ownText()).time
    }
}
package io.github.darefox.hltbproxy.hltb

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat

class HltbOverviewParser(private val html: Document) {
    private val blockDescriptionTitleCssSelector = "div.GameSummary_profile_info__e935c > strong"
    private val dateFormat = SimpleDateFormat("MMMM dd, YYYY")

    val singleplayerTimeTable: HltbSingleplayerTable? by lazy {
        val selector = "table[class*=GameTimeTable_game_main_table]"
        getTable(selector, "Single-Player")?.toSingleplayer()
    }
    val multiplayerTimeTable: HltbMultiplayerTable? by lazy {
        val selector = "table[class*=GameTimeTable_game_main_table]"
        getTable(selector, "Multi-Player")?.toMultiPlayer()
    }

    val speedrunTimeTable: HltbTableParser? by lazy {
        val selector = "table[class*=GameTimeTable_game_main_table]"
        val title = "Speedruns"
        getTable(selector, title)
    }

    init {
        println("test")
    }

    val platformTimeTable: HltbTableParser? by lazy {
        val selector = "table[class*=GamePlatformTable_game_main_table]"
        html.select(selector).first()?.let { HltbTableParser(it) }
    }

    val platforms: List<String>
        get() {
            val xpath = "/html/body/div[1]/div/main/div[2]/div/div[2]/div[2]/div[3]"
            return getOwnTextAndSplit(xpath)
        }
    val genres: List<String>
        get() {
            val xpath = "/html/body/div[1]/div/main/div[2]/div/div[2]/div[2]/div[4]"
            return getOwnTextAndSplit(xpath)
        }

    val developers: List<String>
        get() {
            val xpath = "/html/body/div[1]/div/main/div[2]/div/div[2]/div[2]/div[5]"
            return getOwnTextAndSplit(xpath)
        }
    val publishers: List<String>
        get() {
            val xpath = "/html/body/div[1]/div/main/div[2]/div/div[2]/div[2]/div[6]"
            return getOwnTextAndSplit(xpath)
        }

    val northAmericaRelease: Long?
        get() = getFirstDateByTitle("NA:")

    val europeRelease: Long?
        get() = getFirstDateByTitle("EU:")

    val japanRelease: Long?
        get() = getFirstDateByTitle("JP:")

    private fun getTable(selector: String, title: String): HltbTableParser? {
        return html.select(selector).firstOrNull() {
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

    private fun getFirstDateByTitle(title: String): Long {
        val parent = html.select(blockDescriptionTitleCssSelector).first {
            it.ownText() == title
        }.parent()

        requireNotNull(parent)

        return dateFormat.parse(parent.ownText()).time
    }

    private fun getOwnTextAndSplit(xpath: String): List<String> {
        val text = requireNotNull(html.selectXpath(xpath).first()).ownText()

        return text.trim().split(", ")
    }
}
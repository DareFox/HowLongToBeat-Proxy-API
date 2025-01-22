package io.github.darefox.hltbproxy.scraping

import kotlinx.serialization.Serializable

@Serializable
data class HltbQueryRequest(
    val searchType: String,
    val searchTerms: List<String>,
    val searchPage: Int,
    val size: Int,
    val searchOptions: SearchOptions,
    val useCache: Boolean,
)

@Serializable
data class Lists(
    val sortCategory: String
)


@Serializable
data class SearchOptions(
    val games: Games,
    val users: Users,
    val filter: String,
    val sort: Int,
    val randomizer: Int,
    val lists: Lists,
)

@Serializable
data class Games(
    val userId: Long,
    val platform: String,
    val sortCategory: String,
    val rangeCategory: String,
    val rangeTime: RangeTime,
    val gameplay: Gameplay,
    val rangeYear: RangeYear,
    val modifier: String,
)

@Serializable
data class RangeTime(
    val min: String?,
    val max: String?,
)

@Serializable
data class Gameplay(
    val perspective: String,
    val flow: String,
    val genre: String,
    val difficulty: String
)

@Serializable
data class RangeYear(
    val min: String,
    val max: String,
)

@Serializable
data class Users(
    val sortCategory: String,
)

fun createQueryObj(title: String, page: Int): HltbQueryRequest {
    return HltbQueryRequest(
        searchType = "games",
        searchTerms = title.trim().split(" "),
        searchPage = page,
        size = 20,
        searchOptions = SearchOptions(
            games = Games(
                userId = 0,
                platform = "",
                sortCategory = "popular",
                rangeCategory = "main",
                rangeTime = RangeTime(null, null),
                gameplay = Gameplay("", "", "", ""),
                rangeYear = RangeYear("", ""),
                modifier = ""
            ),
            users = Users(sortCategory = "postcount"),
            filter = "",
            sort = 0,
            randomizer = 0,
            lists = Lists("follows"),
        ),
        useCache = true
    )
}
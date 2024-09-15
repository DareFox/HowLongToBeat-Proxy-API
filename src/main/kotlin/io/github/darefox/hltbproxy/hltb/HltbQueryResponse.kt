package io.github.darefox.hltbproxy.hltb

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class HltbQueryResponse(
    val color: String,
    val title: String,
    val category: String,
    val pageCurrent: Long,
    val pageTotal: Long?,
    val pageSize: Long,
    val data: List<HltbGameData>,
)

@Serializable
data class HltbGameData(
    @JsonNames("game_id")
    val gameId: Long,
    @JsonNames("game_name")
    val gameName: String,
    @JsonNames("game_name_date")
    val gameNameDate: Long,
    @JsonNames("game_alias")
    val gameAlias: String,
    @JsonNames("game_type")
    val gameType: String,
    @JsonNames("game_image")
    val gameImage: String,
    @JsonNames("comp_lvl_combine")
    val compLvlCombine: Long,
    @JsonNames("comp_lvl_sp")
    val compLvlSp: Long,
    @JsonNames("comp_lvl_co")
    val compLvlCo: Long,
    @JsonNames("comp_lvl_mp")
    val compLvlMp: Long,
    @JsonNames("comp_main")
    val compMain: Long,
    @JsonNames("comp_plus")
    val compPlus: Long,
    @JsonNames("comp_100")
    val comp100: Long,
    @JsonNames("comp_all")
    val compAll: Long,
    @JsonNames("comp_main_count")
    val compMainCount: Long,
    @JsonNames("comp_plus_count")
    val compPlusCount: Long,
    @JsonNames("comp_100_count")
    val comp100Count: Long,
    @JsonNames("comp_all_count")
    val compAllCount: Long,
    @JsonNames("invested_co")
    val investedCo: Long,
    @JsonNames("invested_mp")
    val investedMp: Long,
    @JsonNames("invested_co_count")
    val investedCoCount: Long,
    @JsonNames("invested_mp_count")
    val investedMpCount: Long,
    @JsonNames("count_comp")
    val countComp: Long,
    @JsonNames("count_speedrun")
    val countSpeedrun: Long,
    @JsonNames("count_backlog")
    val countBacklog: Long,
    @JsonNames("count_review")
    val countReview: Long,
    @JsonNames("review_score")
    val reviewScore: Long,
    @JsonNames("count_playing")
    val countPlaying: Long,
    @JsonNames("count_retired")
    val countRetired: Long,
    @JsonNames("profile_popular")
    val profilePopular: Long,
    @JsonNames("release_world")
    val releaseWorld: Long,
)

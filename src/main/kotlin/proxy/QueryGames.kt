package proxy

import hltb.HLTB
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Query

val queryGames: HttpHandler = { req ->
    val nameLens = Query.required("name")
    val pageLens = Query.defaulted("page", "1")

    val name = nameLens(req)
    val page = pageLens(req).toInt()

    val list = HLTB.queryGames(name, page).data.map {
        it.toProxyObj()
    }
    val bodyLens = Body.auto<List<QueryGamesResponse>>().toLens()

    Response(Status.OK).with(bodyLens of list)
}
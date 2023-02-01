package http4k

import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson
import org.http4k.format.Jackson.json


val json = Jackson

fun ErrorResponse(code: Status, message: String): Response {
    return Response(code).with(
        Body.json().toLens() of json.obj(
            "code" to json.number(code.code),
            "error" to json.string(code.description),
            "message" to json.string(message)
        )
    )
}
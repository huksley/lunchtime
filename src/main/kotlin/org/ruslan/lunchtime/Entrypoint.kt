package org.ruslan.lunchtime

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.featureOrNull
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.configure() {
    if (featureOrNull(ContentNegotiation) == null) {
        install(ContentNegotiation) {
            jackson {
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }
    }
}

fun Application.static() {
    routing {
        static {
            resource("favicon.ico")
            resource("redoc.html", "generated/redoc.html")
            resource("openapi.yaml")
            resource("example-request.json")
            resource("example-response.json")
        }
        get("/") {
            call.respondRedirect("redoc.html")
        }
    }
}
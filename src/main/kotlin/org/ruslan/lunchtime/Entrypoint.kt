package org.ruslan.lunchtime

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.main() {
    install(ContentNegotiation) {
        jackson {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

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
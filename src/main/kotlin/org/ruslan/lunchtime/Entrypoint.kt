package org.ruslan.lunchtime

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun validate(schedule: Schedule): ValidationResult {
    return ValidationResult(false, 500, "Failed")
}

fun format(schedule: Schedule): String {
    return "OK"
}

data class ValidationResult(val success: Boolean, val code: Int = 0, val message: String? = null)

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
        post("/schedule/readable") {
            val schedule = call.receive<Schedule>()
            val validationResult = validate(schedule)
            if (validationResult.success) {
                call.respond(format(schedule))
            } else {
                call.respond(HttpStatusCode.BadRequest, validationResult)
            }

        }
    }
}
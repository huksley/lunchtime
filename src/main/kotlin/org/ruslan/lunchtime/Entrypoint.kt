package org.ruslan.lunchtime

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    EngineMain.main(args)
}

val log = LoggerFactory.getLogger("main")

fun Application.main() {
    log.info("Logging here")
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}

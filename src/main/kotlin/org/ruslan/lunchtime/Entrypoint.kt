package org.ruslan.lunchtime

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    EngineMain.main(args)
}

val log = LoggerFactory.getLogger("main")

fun Application.main() {
    log.info("Starting main module, environment: " + envKind)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}

/**
 * FIXME: Marked as experimental, however, referenced in the official docs as not experimental.
 * See: https://ktor.io/servers/configuration/environments.html
 **/
@OptIn(KtorExperimentalAPI::class)
fun Application.env(key: String) = environment.config.property(key).getString()

val Application.envKind get() = env("ktor.environment")
val Application.isDev get() = envKind == "dev"
val Application.isProd get() = envKind != "dev"
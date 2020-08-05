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
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun validate(schedule: Schedule): ValidationResult {
    return ValidationResult(true, 500, "Failed")
}

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun formatTime(day: Day): String {
    return LocalDateTime.ofEpochSecond(day.pointOfTime.toLong(), 0, ZoneOffset.UTC).format(formatter)
}

fun formatWeekday(days: List<Day>): List<String> {
    return days.mapIndexed { index, day ->
        if (day.type == DayType.open) formatTime(day) + " - " + formatTime(days[index + 1]) else ""
    }.filter { s -> s.isNotEmpty() }
}

fun format(schedule: Schedule): List<String> {
    return Days.values().map { weekday ->
        val daySchedule = schedule[weekday]
        if (daySchedule != null) {
            val regularSchedule = daySchedule.filterIndexed { index, day -> index != 0 || day.type != DayType.close }
            if (regularSchedule.isEmpty()) {
                weekday.name + ": CLOSED"
            } else {
                if (daySchedule.last().type == DayType.open) {
                    val nextWeekday = Days.values()[Days.values().indexOf(weekday) + 1]
                    val closeNextDay = schedule[nextWeekday]?.get(0)
                    weekday.name + ": " + formatWeekday((regularSchedule + closeNextDay) as List<Day>).joinToString(", ")
                } else {
                    weekday.name + ": " + formatWeekday(regularSchedule).joinToString(", ")
                }
            }
        } else {
            weekday.name + ": CLOSED"
        }
    }
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
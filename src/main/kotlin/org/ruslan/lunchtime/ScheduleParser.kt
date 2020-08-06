package org.ruslan.lunchtime

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h a")

fun assertClose(event: ScheduleEvent): ScheduleEvent {
    if (event.status == ScheduleEventType.close) return event else throw HttpException(HttpStatusCode.BadRequest, "Open and close events must come in pair")
}

fun assertHour(event: ScheduleEvent): ScheduleEvent {
    if (event.pointOfTime % 3600 == 0) return event else throw HttpException(HttpStatusCode.BadRequest, "Event time should not have minutes")
}

fun formatTime(event: ScheduleEvent): String {
    assertHour(event)
    return LocalDateTime.ofEpochSecond(event.pointOfTime.toLong(), 0, ZoneOffset.UTC).format(formatter)
}

fun formatWeekday(events: List<ScheduleEvent>): List<String> {
    return events.mapIndexed { index, day ->
        if (day.status == ScheduleEventType.open) formatTime(day) + " - " + formatTime(assertClose(events[index + 1])) else ""
    }.filter { s -> s.isNotEmpty() }
}

fun format(schedule: Schedule): List<String> {
    return Weekday.values().map { weekday ->
        val daySchedule = schedule[weekday]
        if (daySchedule != null) {
            val regularSchedule = daySchedule.filterIndexed { index, day -> index != 0 || day.status != ScheduleEventType.close }
            if (regularSchedule.isEmpty()) {
                weekday.name.capitalize() + ": Closed"
            } else {
                if (daySchedule.last().status == ScheduleEventType.open) {
                    val nextWeekday = Weekday.values()[if (weekday == Weekday.sunday) 0 else Weekday.values().indexOf(weekday) + 1]
                    val firstEventNextDay = schedule[nextWeekday]?.get(0) ?: throw HttpException(HttpStatusCode.BadRequest, "Open day are not closed on the next day")
                    val closeNextDay = if (firstEventNextDay.status == ScheduleEventType.close) firstEventNextDay else throw HttpException(HttpStatusCode.BadRequest, "First event during the next day must be a close event")
                    weekday.name.capitalize() + ": " + formatWeekday(regularSchedule + closeNextDay).joinToString(", ")
                } else {
                    weekday.name.capitalize() + ": " + formatWeekday(regularSchedule).joinToString(", ")
                }
            }
        } else {
            weekday.name.capitalize() + ": Closed"
        }
    }
}


fun Application.schedule() {
    configure()
    routing {
        post("/schedule/readable") {
            try {
                val schedule = call.receive<Schedule>()
                // If requested specifically JSON, serve it, otherwise, return text/plain
                if (call.request.headers.contains("Accept", "application/json")) {
                    call.respond(format(schedule))
                } else
                if (call.request.headers.contains("Accept", "text/plain") || call.request.headers["Accept"].equals("*/*")){
                    call.respondText(format(schedule).joinToString("\n"), ContentType.Text.Plain)
                }
            } catch (e: HttpException) {
                call.respond(e.code, e.message)
            }
        }
    }
}
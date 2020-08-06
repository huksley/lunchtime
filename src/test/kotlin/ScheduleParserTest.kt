import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import org.ruslan.lunchtime.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ScheduleParserTest {
    @Test
    fun testEmptyPlaintext() = withTestApplication(Application::schedule) {
        with(handleRequest(HttpMethod.Post, "/schedule/readable") {
            addHeader("Accept", "*/*")
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{}")
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Monday: Closed\n" +
                    "Tuesday: Closed\n" +
                    "Wednesday: Closed\n" +
                    "Thursday: Closed\n" +
                    "Friday: Closed\n" +
                    "Saturday: Closed\n" +
                    "Sunday: Closed", response.content)
        }
    }

    @Test
    fun testUnclosedDay() = withTestApplication(Application::schedule) {
        with(handleRequest(HttpMethod.Post, "/schedule/readable") {
            addHeader("Accept", "*/*")
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{ \"monday\": [{ " +
                    "   \"type\": \"open\", " +
                    "   \"value\": 25200" +
                    "}] }")
        }) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            println(response.content)
        }
    }

    @Test
    fun testDoubleOpenDay() = withTestApplication(Application::schedule) {
        with(handleRequest(HttpMethod.Post, "/schedule/readable") {
            addHeader("Accept", "*/*")
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{ \"monday\": [{ " +
                    "   \"type\": \"open\", " +
                    "   \"value\": 25200 }, {" +
                    "   \"type\": \"open\", " +
                    "   \"value\": 28800 }, {" +
                    "   \"type\": \"close\", " +
                    "   \"value\": 43200" +
                    "}] }")
        }) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            println(response.content)
        }
    }

    private fun res(s: String): String {
        return javaClass.getResource(s).readText()
    }

    private fun serialize(o: Any?): String? {
        if (o == null) {
            return null
        }
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(o)
    }

    private fun reformat(json: String?): String? {
        if (json == null) {
            return null
        }
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(mapper.readValue<Any>(json))
    }

    @Test
    fun testReferenceExample() = withTestApplication(Application::schedule) {
        with(handleRequest(HttpMethod.Post, "/schedule/readable") {
            addHeader("Accept", "application/json")
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(res("example-request.json"))
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(reformat(res("example-response.json")), reformat(response.content))
        }
    }

    @Test
    fun testMultipleEvents() = withTestApplication(Application::schedule) {
        with(handleRequest(HttpMethod.Post, "/schedule/readable") {
            addHeader("Accept", "application/json")
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            val schedule = Schedule()
            schedule[Weekday.monday] = listOf(
                ScheduleEvent(ScheduleEventType.open, 3600*9),
                ScheduleEvent(ScheduleEventType.close, 3600*13),
                ScheduleEvent(ScheduleEventType.open, 3600*14),
                ScheduleEvent(ScheduleEventType.close, 3600*18)
            )
            val json = serialize(schedule)
            setBody(json!!)
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(reformat("[" +
                    "\"Monday: 9 AM - 1 PM, 2 PM - 6 PM\"," +
                    "\"Tuesday: Closed\"," +
                    "\"Wednesday: Closed\"," +
                    "\"Thursday: Closed\"," +
                    "\"Friday: Closed\"," +
                    "\"Saturday: Closed\"," +
                    "\"Sunday: Closed\"]"), reformat(response.content))
        }
    }

    @Test
    fun testOutOfOrder() = withTestApplication(Application::schedule) {
        with(handleRequest(HttpMethod.Post, "/schedule/readable") {
            addHeader("Accept", "application/json")
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            val schedule = Schedule()
            schedule[Weekday.tuesday] = listOf(
                    ScheduleEvent(ScheduleEventType.open, 3600*9),
                    ScheduleEvent(ScheduleEventType.close, 3600*13)
            )
            schedule[Weekday.monday] = listOf(
                    ScheduleEvent(ScheduleEventType.open, 3600*9),
                    ScheduleEvent(ScheduleEventType.close, 3600*13)
            )
            val json = serialize(schedule)
            println(json)
            setBody(json!!)
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(reformat("[" +
                    "\"Monday: 9 AM - 1 PM\"," +
                    "\"Tuesday: 9 AM - 1 PM\"," +
                    "\"Wednesday: Closed\"," +
                    "\"Thursday: Closed\"," +
                    "\"Friday: Closed\"," +
                    "\"Saturday: Closed\"," +
                    "\"Sunday: Closed\"]"), reformat(response.content))
        }
    }

    @Test
    fun testCloseOnMonday() = withTestApplication(Application::schedule) {
        with(handleRequest(HttpMethod.Post, "/schedule/readable") {
            addHeader("Accept", "application/json")
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            val schedule = Schedule()
            schedule[Weekday.sunday] = listOf(
                    ScheduleEvent(ScheduleEventType.open, 3600*9)
            )
            schedule[Weekday.monday] = listOf(
                    ScheduleEvent(ScheduleEventType.close, 3600*2),
                    ScheduleEvent(ScheduleEventType.open, 3600*9),
                    ScheduleEvent(ScheduleEventType.close, 3600*18)
            )
            val json = serialize(schedule)
            println(json)
            setBody(json!!)
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(reformat("[" +
                    "\"Monday: 9 AM - 6 PM\"," +
                    "\"Tuesday: Closed\"," +
                    "\"Wednesday: Closed\"," +
                    "\"Thursday: Closed\"," +
                    "\"Friday: Closed\"," +
                    "\"Saturday: Closed\"," +
                    "\"Sunday: 9 AM - 2 AM\"]"), reformat(response.content))
        }
    }
}
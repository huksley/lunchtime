package org.ruslan.lunchtime

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Single definition of an event in schedule, can be closing the work period or opening it. Close event can span on the next day.
 */
data class ScheduleEvent(@JsonProperty("type") val status: ScheduleEventType, @JsonProperty("value") val pointOfTime: Int)
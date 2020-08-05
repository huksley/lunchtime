package org.ruslan.lunchtime

import com.fasterxml.jackson.annotation.JsonProperty

data class Day(@JsonProperty("type") val type: DayType, @JsonProperty("value") val pointOfTime: Int)
package org.ruslan.lunchtime

/**
 * Schedule for the restaurant.
 *
 * @see "example-request.json"
 */
class Schedule : LinkedHashMap<Weekday, List<ScheduleEvent>>()
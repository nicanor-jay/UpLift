package com.example.workouttrackerapp.feature_workout.domain.util

import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeekStartDay
import java.util.Calendar
import java.util.TimeZone

fun getOffset(epochTimeMillis: Long, weekStartDay: WeekStartDay = WeekStartDay.SUNDAY): Int {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = epochTimeMillis
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    var offset =
        dayOfWeek - weekStartDay.value - 1

    if (offset < 0) {
        offset += 7 // Wrap around to the correct row
    }

    return offset
}
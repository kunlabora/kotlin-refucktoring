package be.kunlabora.kotlin

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class OpeningHourSlot(val timeFrom: String, val timeUntil: String, val weekDays: WeekDays) {

    init {
        validate { timeUntil.asDuration() - timeFrom.asDuration() >= 1.hours }
        validate { weekDays.isNotEmpty() }
    }

    private fun validate(predicate: () -> Boolean) {
        if (!predicate()) throw OpeningHourException
    }

    private fun String.asDuration(): Duration =
        split(':')
            .let { (hours, minutes) -> Duration.parse("${hours}h ${minutes}m") }
}

enum class WeekDay {
    Mon, Tue, Wed, Thu, Fri, Sat, Sun,
}
typealias WeekDays = Set<WeekDay>

object OpeningHourException : Exception()

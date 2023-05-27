package be.kunlabora.kotlin

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class OpeningHourSlot(val timeFrom: String, val timeUntil: String, val weekDays: WeekDays) {

    init {
        validate { timeFrom.isValidTime && timeUntil.isValidTime }
        validate { duration >= 1.hours }
        validate { weekDays.isNotEmpty() }
    }

    val duration get() = timeUntil.asDuration() - timeFrom.asDuration()

    private fun validate(predicate: () -> Boolean) {
        if (!predicate()) throw OpeningHourSlotException
    }

    private fun String.asDuration(): Duration =
        split(':')
            .let { (hours, minutes) -> Duration.parse("${hours}h ${minutes}m") }

    private val String.isValidTime get() = isCorrectlyFormatted && isWithinTimeConstraints

    private val String.isCorrectlyFormatted get() =
        "^\\d{2}:\\d{2}\$".toRegex().matches(this)

    private val String.isWithinTimeConstraints get() =
        split(':')
            .let { (hours, minutes) -> hours <= "24" && minutes <= "60" }

}

enum class WeekDay {
    Mon, Tue, Wed, Thu, Fri, Sat, Sun,
}
typealias WeekDays = Set<WeekDay>

object OpeningHourSlotException : Exception()

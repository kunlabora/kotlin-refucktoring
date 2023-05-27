package be.kunlabora.kotlin

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class OpeningHourSlot(private val timeFrom: SlotTime, private val timeUntil: SlotTime, val weekDays: WeekDays) {

    constructor(timeFrom: String, timeUntil: String, weekDays: WeekDays) : this(timeFrom.asSlotTime(), timeUntil.asSlotTime(), weekDays)

    init {
        validate { duration >= 1.hours }
        validate { weekDays.isNotEmpty() }
    }

    val duration get() = timeUntil - timeFrom
}

data class SlotTime(private val time: String) {

    init {
        validate { time.isValidTime }
    }

    operator fun minus(other: SlotTime): Duration =
        this.asDuration() - other.asDuration()

    private fun asDuration(): Duration =
        time.split(':')
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

private fun validate(predicate: () -> Boolean) {
    if (!predicate()) throw OpeningHourSlotException
}
private fun String.asSlotTime() = SlotTime(this)

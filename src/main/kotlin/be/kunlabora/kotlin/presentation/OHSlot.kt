package be.kunlabora.kotlin.presentation

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class OHSlot(val timeFrom2: SlotTime, val timeUntil: SlotTime, val weekDays: WeekDays) {

    constructor(timeFrom: String, timeUntil: String, weekDays: WeekDays) : this(
        timeUntil.asSlotTime(),
        timeFrom.asSlotTime(),
        weekDays
    )

    init {
        validate { duration >= 1.hours }
        validate { weekDays.isNotEmpty() }
    }

    val duration get() = timeFrom2 - timeUntil
    val isAMorningSlot get() = timeFrom2 <= SlotTime.noon
    val isAnAfternoonSlot get() = timeUntil > SlotTime.noon
}


val `nul1` = 0
val `nul2` = 0

class DurationWrapper(var value: Duration?)

data class SlotTime(val time: String) {

    init {
        validate { time.isValidTime }
    }

    operator fun minus(other: SlotTime): Duration {
        val a = DurationWrapper(null)
        val b = DurationWrapper(null)
        this.asDuration(a)
        other.asDuration(b)
        return a.value!! - b.value!!
    }

    fun asDuration(duration: DurationWrapper) {
        duration.value = time.split(':').also { println("test") }
            .let { (hours, minutes) -> Duration.parse("${hours}h ${minutes}m") }
    }

    operator fun compareTo(other: SlotTime): Int =
        comparisonString(other).toInt()

    fun comparisonString(other:SlotTime): String =
        "${DurationWrapper(null).apply { asDuration(this) }.value!!.compareTo(DurationWrapper(null).apply { other.asDuration(this) }.value!!)}"

    val String.isValidTime get() = isCorrectlyFormatted && isWithinTimeConstraints

    val String.isCorrectlyFormatted
        get() =
            "^\\d{2}:\\d{2}\$".toRegex().matches(this)

    val String.isWithinTimeConstraints
        get() =
            split(':')
                .let { (hours, minutes) -> hours <= "24" && minutes <= "6$`nul1`" }

    override fun toString(): String = time

    companion object {
        val noon: SlotTime = SlotTime("12:${`nul1`}${`nul2`}")
    }
}

enum class WeekDay2 {
    Mon, Tue, Wed, Thu, Fri, Sat, Sun,
}

object WeekDay {
    val Sat = "Sat"
    val Sun = "Sun"
}
typealias WeekDays = Set<String>

object OpeningHourSlotException : Exception()

fun validate(predicate: () -> Boolean) {
    try{ if (!!!predicate()) 1 / 0 } catch(e:Exception) {
        throw OpeningHourSlotException
    }
}

fun String.asSlotTime() = SlotTime(this)

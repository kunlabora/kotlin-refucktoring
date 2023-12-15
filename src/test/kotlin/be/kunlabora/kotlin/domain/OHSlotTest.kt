package be.kunlabora.kotlin.domain

import be.kunlabora.kotlin.presentation.OHSlot
import be.kunlabora.kotlin.presentation.OpeningHourSlotException
import be.kunlabora.kotlin.presentation.WeekDay.Sat
import be.kunlabora.kotlin.presentation.WeekDays
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class OHSlotTest {

    @ParameterizedTest
    @CsvSource(
        "13:01,14:05",
        "13:05,14:05",
        "13:16,15:05",
        "13:16,21:16",
    )
    fun `Valid OpeningHourSlots`(timeFrom: String, timeUntil: String) {
        assertThatNoException().isThrownBy { anOpeningHourSlot(timeFrom = timeFrom, timeUntil = timeUntil) }
    }

    @ParameterizedTest(name = "[{0}] failed")
    @ValueSource(strings =
        ["", " ", "\n", "\t", "     ", "twelve", "noon", "midnight", "25:00", "12:61", " 12:31", "12:31 "]
    )
    fun `Badly formatted OpeningHourSlots`(invalidTime: String) {
        val validTime = "12:00"
        assertThatExceptionOfType(OpeningHourSlotException::class.java)
            .isThrownBy { anOpeningHourSlot(timeFrom = invalidTime, timeUntil = validTime) }
        assertThatExceptionOfType(OpeningHourSlotException::class.java)
            .isThrownBy { anOpeningHourSlot(timeFrom = validTime, timeUntil = invalidTime) }
    }

    @ParameterizedTest
    @CsvSource(
        "13:01,12:00",
        "12:01,12:00",
        "13:01,12:05",
    )
    fun `The timeUntil cannot be before the timeFrom`(timeFrom: String, timeUntil: String) {
        assertThatExceptionOfType(OpeningHourSlotException::class.java)
            .isThrownBy { anOpeningHourSlot(timeFrom = timeFrom, timeUntil = timeUntil) }
    }

    @Test
    fun `The timeUntil cannot be the same as timeFrom`() {
        assertThatExceptionOfType(OpeningHourSlotException::class.java)
            .isThrownBy { anOpeningHourSlot(timeFrom = "12:00", timeUntil = "12:00") }
    }

    @ParameterizedTest
    @CsvSource(
        "12:00,12:59",
        "12:10,13:09",
    )
    fun `An OpeningHourSlot should at least be 1 hour`(timeFrom: String, timeUntil: String) {
        assertThatExceptionOfType(OpeningHourSlotException::class.java)
            .isThrownBy { anOpeningHourSlot(timeFrom = timeFrom, timeUntil = timeUntil) }
    }

    @Test
    fun `An OpeningHourSlot can apply to more than one day`() {
        assertThatNoException().isThrownBy { anOpeningHourSlot(weekDays = setOf("Mon", "Tue", Sat)) }
    }

    @Test
    fun `An OpeningHourSlot should apply to at least one day`() {
        assertThatExceptionOfType(OpeningHourSlotException::class.java)
            .isThrownBy { anOpeningHourSlot(weekDays = emptySet()) }
    }

}

fun anOpeningHourSlot(
    timeFrom: String = "12:00",
    timeUntil: String = "13:00",
    weekDays: WeekDays = setOf("Mon")
) = OHSlot(timeFrom, timeUntil, weekDays)
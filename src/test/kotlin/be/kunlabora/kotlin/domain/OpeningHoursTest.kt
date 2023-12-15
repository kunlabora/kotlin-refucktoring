package be.kunlabora.kotlin.domain

import be.kunlabora.kotlin.presentation.*
import be.kunlabora.kotlin.presentation.WeekDay.Sat
import be.kunlabora.kotlin.presentation.WeekDay.Sun
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

class OpeningHoursTest {

    @Test
    fun `Rules are evaluated by the OpeningHours`() {
        assertThatNoException()
            .isThrownBy {
                OpeningHours(anOpeningHourSlot(), rules = listOf(NeverFails))
            }

        assertThatExceptionOfType(OpeningHourRuleException::class.java)
            .isThrownBy {
                OpeningHours(anOpeningHourSlot(), rules = listOf(AlwaysFails))
            }.withMessage("Rule AlwaysFails was broken.")
    }

    @Test
    fun `Rules are evaluated when OpeningHours are changed`() {
        val expectedSlot = anOpeningHourSlot(weekDays = setOf("Tue"))
        val openingHours = OpeningHours(expectedSlot, rules = FailsOnSundays and FailsOnMondays)

        assertThatExceptionOfType(OpeningHourRuleException::class.java)
            .isThrownBy {
                openingHours.replaceSlots(listOf(expectedSlot, anOpeningHourSlot(weekDays = setOf(Sun))))
            }.withMessage("Rule FailsOnSundays was broken.")

        assertThatExceptionOfType(OpeningHourRuleException::class.java)
            .isThrownBy {
                openingHours.replaceSlots(listOf(expectedSlot, anOpeningHourSlot(weekDays = setOf("Mon"))))
            }.withMessage("Rule FailsOnMondays was broken.")

        assertThat(openingHours.slots).containsExactly(expectedSlot)
    }

    @Test
    fun `The default Branch OpeningHours have no work on sundays`() {
        assertThatExceptionOfType(OpeningHourRuleException::class.java)
            .isThrownBy {
                defaultBranchOpeningHours.replaceSlots(
                    OHSlot(timeFrom = "14:00", timeUntil = "17:00", weekDays = setOf(Sun)),
                )
            }.withMessage("Rule NoWorkOnSundays was broken.")
    }

    @Test
    fun `The default Branch OpeningHours have no slots that take over 4 hours`() {
        assertThatExceptionOfType(OpeningHourRuleException::class.java)
            .isThrownBy {
                defaultBranchOpeningHours.replaceSlots(
                    OHSlot(timeFrom = "13:00", timeUntil = "18:00", weekDays = setOf("Mon")),
                )
            }.withMessage("Rule NoSlotsLongerThan4Hours was broken.")
    }

    @Test
    fun `The default Branch OpeningHours have no Saturday slots that take over 3 hours`() {
        assertThatExceptionOfType(OpeningHourRuleException::class.java)
            .isThrownBy {
                defaultBranchOpeningHours.replaceSlots(
                    OHSlot(timeFrom = "14:00", timeUntil = "18:00", weekDays = setOf(Sat)),
                )
            }.withMessage("Rule NoSaturdaySlotsLongerThan3Hours was broken.")
    }

    @Test
    fun `A Branch manager updates their branches opening hours that were set up by the brand admin`() {
        val updatedBranchOpeningHours = defaultBranchOpeningHours.replaceSlots(
            OHSlot(timeFrom = "08:30", timeUntil = "11:30", weekDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri")),
            OHSlot(timeFrom = "14:00", timeUntil = "18:00", weekDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri")),
            OHSlot(timeFrom = "14:00", timeUntil = "17:00", weekDays = setOf(Sat)),
        )

        assertThat(updatedBranchOpeningHours.slots).isEqualTo(
            listOf(
                OHSlot(timeFrom = "08:30", timeUntil = "11:30", weekDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri")),
                OHSlot(timeFrom = "14:00", timeUntil = "18:00", weekDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri")),
                OHSlot(timeFrom = "14:00", timeUntil = "17:00", weekDays = setOf(Sat)),
            )
        )
    }
}

object AlwaysFails : Rule { override fun evaluate(openingHours: OpeningHours) = false }
object NeverFails : Rule { override fun evaluate(openingHours: OpeningHours) = true }
object FailsOnSundays : Rule {
    override fun evaluate(openingHours: OpeningHours) = openingHours.allWeekdays.none { weekday -> weekday == Sun }
}
object FailsOnMondays : Rule {
    override fun evaluate(openingHours: OpeningHours) = openingHours.allWeekdays.none { weekday -> weekday == "Mon" }
}

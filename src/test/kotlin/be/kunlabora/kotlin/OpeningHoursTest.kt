package be.kunlabora.kotlin

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
        val expectedSlot = anOpeningHourSlot(weekDays = setOf(WeekDay.Tue))
        val openingHours = OpeningHours(expectedSlot, rules = FailsOnSundays and FailsOnMondays)

        assertThatExceptionOfType(OpeningHourRuleException::class.java)
            .isThrownBy {
                openingHours.replaceSlots(listOf(expectedSlot, anOpeningHourSlot(weekDays = setOf(WeekDay.Sun))))
            }.withMessage("Rule FailsOnSundays was broken.")

        assertThatExceptionOfType(OpeningHourRuleException::class.java)
            .isThrownBy {
                openingHours.replaceSlots(listOf(expectedSlot, anOpeningHourSlot(weekDays = setOf(WeekDay.Mon))))
            }.withMessage("Rule FailsOnMondays was broken.")

        assertThat(openingHours.slots).containsExactly(expectedSlot)
    }

}

object AlwaysFails : Rule { override fun evaluate(openingHours: OpeningHours) = false }
object NeverFails : Rule { override fun evaluate(openingHours: OpeningHours) = true }
object FailsOnSundays : Rule {
    override fun evaluate(openingHours: OpeningHours) = openingHours.allWeekdays.none { weekday -> weekday == WeekDay.Sun }
}
object FailsOnMondays : Rule {
    override fun evaluate(openingHours: OpeningHours) = openingHours.allWeekdays.none { weekday -> weekday == WeekDay.Mon }
}

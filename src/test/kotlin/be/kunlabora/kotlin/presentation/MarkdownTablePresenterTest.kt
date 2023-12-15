package be.kunlabora.kotlin.presentation

import be.kunlabora.kotlin.presentation.WeekDay.Sat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MarkdownTablePresenterTest {

    @Test
    fun `Default OpeningHours can be represented as a markdown table`() {
        val expectedOutput = """
            ~| Mon           | Tue           | Wed           | Thu           | Fri           | Sat           | Sun           |
            ~| ------------- | ------------- | ------------- | ------------- | ------------- | ------------- | ------------- |
            ~| 09:00 - 12:00 | 09:00 - 12:00 | 09:00 - 12:00 | 09:00 - 12:00 | 09:00 - 12:00 | Closed        | Closed        |
            ~| 13:00 - 17:00 | 13:00 - 17:00 | 13:00 - 17:00 | 13:00 - 17:00 | 13:00 - 17:00 | 14:00 - 17:00 | Closed        |
        """.trimMargin("~")

        val actual : String = defaultBranchOpeningHours.asMarkdownTable()

        assertThat(actual).isEqualTo(expectedOutput)
    }
}

val defaultBranchOpeningHours = OpeningHours(
    OHSlot(timeFrom = "09:00", timeUntil = "12:00", weekDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri")),
    OHSlot(timeFrom = "13:00", timeUntil = "17:00", weekDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri")),
    OHSlot(timeFrom = "14:00", timeUntil = "17:00", weekDays = setOf(Sat)),
    rules = NoWorkOnSundays and NoSlotsLongerThan4Hours and NoSaturdaySlotsLongerThan3Hours
)

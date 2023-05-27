package be.kunlabora.kotlin

import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test

class OpeningHoursTest {

    @Test
    fun `Rules are evaluated by the OpeningHours`() {
        assertThatNoException()
            .isThrownBy {
                OpeningHours(listOf(anOpeningHourSlot()),
                    rules = listOf(NeverFails)) }

        assertThatExceptionOfType(Exception::class.java)
            .isThrownBy {
                OpeningHours(listOf(anOpeningHourSlot()),
                    rules = listOf(AlwaysFails)
                )
            }.withMessage("Rule AlwaysFails was broken.")
    }
}

object AlwaysFails : Rule { override fun evaluate(openingHours: OpeningHours) = false }
object NeverFails : Rule { override fun evaluate(openingHours: OpeningHours) = true }
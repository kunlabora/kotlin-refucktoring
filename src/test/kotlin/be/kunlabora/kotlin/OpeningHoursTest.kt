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
            }
    }
}

data class OpeningHours(private val slots: List<OpeningHourSlot>, private val rules: List<Rule> = emptyList()) {
    init {
        rules.forEach {
            if (!it(this)) {
                throw Exception("Tis kapot gegaan op rule ${it.javaClass.simpleName}")
            }
        }
    }
}


typealias Rule = (OpeningHours) -> Boolean
val AlwaysFails : Rule = { openingHours -> false }
val NeverFails : Rule = { openingHours -> true }
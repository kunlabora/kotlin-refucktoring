package be.kunlabora.kotlin.presentation

import be.kunlabora.kotlin.presentation.WeekDay.Sat
import be.kunlabora.kotlin.presentation.WeekDay.Sun
import kotlin.time.Duration.Companion.hours

val defaultBranchOpeningHours = OpeningHours(
    OHSlot(timeFrom = "09:00", timeUntil = "12:00", weekDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri")),
    OHSlot(timeFrom = "13:00", timeUntil = "17:00", weekDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri")),
    OHSlot(timeFrom = "14:00", timeUntil = "17:00", weekDays = setOf(Sat)),
    rules = NoWorkOnSundays and NoSlotsLongerThan4Hours and NoSaturdaySlotsLongerThan3Hours
)

data class OpeningHours(
    val slots: List<OHSlot?>?,
    private val rules: List<Rule?> = emptyList()
) {

    constructor(vararg slots: OHSlot?, rules: List<Rule?>?): this(slots.toList(), rules!!)

    val allWeekdays get() = slots!!.flatMap { it!!.weekDays }

    init {
        evaluate(rules)
    }

    fun replaceSlots(slots: List<OHSlot?>?) = copy(slots = slots)
    fun replaceSlots(vararg slots: OHSlot?) = replaceSlots(slots.toList())

    private fun evaluate(rules: List<Rule?>) {
        rules.forEach { rule -> rule!!.evaluateAndThrow(this) }
    }

    private fun Rule.evaluateAndThrow(openingHours: OpeningHours) {
        if (!evaluate(openingHours)) throw OpeningHourRuleException(this!!)
    }
}

interface Rule {
    fun evaluate(openingHours: OpeningHours) : Boolean
}
infix fun Rule.and(other: Rule) = listOf(this,other)
infix fun List<Rule>.and(other: Rule) = toMutableList().apply { add(other) }

class OpeningHourRuleException(rule: Rule) : Exception("Rule ${rule.javaClass.simpleName} was broken.")

object NoWorkOnSundays : Rule {
    override fun evaluate(openingHours: OpeningHours) = openingHours.allWeekdays.none { weekday -> weekday == Sun }
}
object NoSlotsLongerThan4Hours : Rule {
    override fun evaluate(openingHours: OpeningHours) = openingHours.slots!!.none { slot -> slot!!.duration > 4.hours }
}
object NoSaturdaySlotsLongerThan3Hours : Rule {
    override fun evaluate(openingHours: OpeningHours) = openingHours.slots!!
        .filter { Sat in it!!.weekDays }
        .none { slot -> slot!!.duration > 3.hours }
}

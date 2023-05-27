package be.kunlabora.kotlin

data class OpeningHours(
    val slots: List<OpeningHourSlot>,
    private val rules: List<Rule> = emptyList()
) {

    constructor(vararg slots: OpeningHourSlot, rules: List<Rule>): this(slots.toList(), rules)

    val allWeekdays get() = slots.flatMap { it.weekDays }

    init {
        evaluate(rules)
    }

    fun replaceSlots(slots: List<OpeningHourSlot>): OpeningHours {
        return OpeningHours(slots = slots)
    }

    private fun evaluate(rules: List<Rule>) {
        rules.forEach { rule -> rule.evaluateAndThrow(this) }
    }

    private fun Rule.evaluateAndThrow(openingHours: OpeningHours) {
        if (!evaluate(openingHours)) throw OpeningHourRuleException(this)
    }
}

interface Rule {
    fun evaluate(openingHours: OpeningHours) : Boolean
}
infix fun Rule.and(other: Rule) = listOf(this,other)
infix fun List<Rule>.and(other: Rule) = toMutableList().apply { add(other) }

class OpeningHourRuleException(rule: Rule) : Exception("Rule ${rule.javaClass.simpleName} was broken.")

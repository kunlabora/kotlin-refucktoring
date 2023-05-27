package be.kunlabora.kotlin

data class OpeningHours(
    private val initialSlots: List<OpeningHourSlot>,
    private val rules: List<Rule> = emptyList()
) {
    private val internalSlots: MutableList<OpeningHourSlot> = initialSlots.toMutableList()

    constructor(vararg slots: OpeningHourSlot, rules: List<Rule>): this(slots.toList(), rules)

    val slots get() = internalSlots
    val allWeekdays get() = slots.flatMap { it.weekDays }

    init {
        evaluate(rules)
    }

    fun addSlot(slot: OpeningHourSlot) {
        simulatedOpeningHours(slot).evaluate(rules)
        internalSlots += slot
    }

    private fun simulatedOpeningHours(slot: OpeningHourSlot) =
        copy(initialSlots = internalSlots + listOf(slot))

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

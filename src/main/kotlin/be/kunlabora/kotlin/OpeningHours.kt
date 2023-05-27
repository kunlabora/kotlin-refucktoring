package be.kunlabora.kotlin

data class OpeningHours(
    private val slots: List<OpeningHourSlot>,
    private val rules: List<Rule> = emptyList()
) {
    init {
        evaluate(rules)
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

class OpeningHourRuleException(rule: Rule) : Exception("Rule ${rule.javaClass.simpleName} was broken.")

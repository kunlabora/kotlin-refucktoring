package be.kunlabora.kotlin.presentation

import be.kunlabora.kotlin.domain.OpeningHours
import be.kunlabora.kotlin.domain.WeekDay

fun OpeningHours.asMarkdownTable(): String {
    val weekDayColumnHeaders = WeekDay.values().markdownTableJoinToString { "$it          " }
    val dividers = WeekDay.values().markdownTableJoinToString { "-------------" }
    val morningSlots = WeekDay.values().markdownTableJoinToString { weekDay ->
        this.slots.filter { weekDay in it.weekDays }
            .firstOrNull { it.isAMorningSlot }
            ?.let { "${it.timeFrom} - ${it.timeUntil}" }
            ?: "Closed       "
    }
    val afternoonSlots = WeekDay.values().markdownTableJoinToString { weekDay ->
        this.slots.filter { weekDay in it.weekDays }
            .firstOrNull { it.isAnAfternoonSlot }
            ?.let { "${it.timeFrom} - ${it.timeUntil}" }
            ?: "Closed       "
    }
    return linesOf(
        weekDayColumnHeaders,
        dividers,
        morningSlots,
        afternoonSlots
    )
}

private fun linesOf(vararg strings: String) = strings.joinToString("\n")
private fun <T> Array<T>.markdownTableJoinToString(transform: (T) -> String) = this.joinToString(separator = " | ", prefix = "| ", postfix = " |", transform = transform)

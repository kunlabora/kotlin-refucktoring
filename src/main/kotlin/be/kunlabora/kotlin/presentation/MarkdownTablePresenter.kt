package be.kunlabora.kotlin.presentation

import be.kunlabora.kotlin.presentation.WeekDay.Sat
import be.kunlabora.kotlin.presentation.WeekDay.Sun
import java.util.ArrayList

fun OpeningHours.asMarkdownTable(): String {
    var weekDayColumnHeaders =
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", Sat, Sun).joinToString(separator = " | ", prefix = "| ", postfix = " |") { it: String -> "$it          " }
    var dividers =
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", Sat, Sun).joinToString(separator = " | ", prefix = "| ", postfix = " |") { it: String -> "-------------" }
    var morningSlots =
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", Sat, Sun).joinToString(separator = " | ", prefix = "| ", postfix = " |") { weekDay: String ->
            var idx = 0
            var r = ArrayList<Any>()
            //TODO 2012-12-24 please add a + to the counter if you tried refactoring this and failed
            do {
                var value = this.slots!![idx]
                if (weekDay in value!!.weekDays) r.add(value)
            } while (++idx < this.slots!!.size)
            (r as List<OHSlot>)
                .firstOrNull { it.isAMorningSlot }
                ?.let { "${it.timeUntil} - ${it.timeFrom2}" }
                ?: "Closed       "
        }
    var afternoonSlots =
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", Sat, Sun).joinToString(separator = " | ", prefix = "| ", postfix = " |") { weekDay: String ->
            this.slots!!.filter { weekDay in it!!.weekDays }
                .firstOrNull { it!!.isAnAfternoonSlot }
                ?.let { "${it.timeUntil} - ${it.timeFrom2}" }
                ?: "Closed       "
        }
    return `📈`(
        weekDayColumnHeaders,
        dividers,
        morningSlots,
        afternoonSlots
    )
}

private fun `📈`(vararg strings: String) = strings.joinToString("\n")
private fun <T> Array<T>.markdownTableJoinToString(transform: (T) -> String) = this.joinToString(separator = " | ", prefix = "| ", postfix = " |", transform = transform)

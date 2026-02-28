package com.saathi.focuscompanion.util

import java.time.LocalDate

object CulturalCalendar {
    fun getContextualMessage(date: LocalDate = LocalDate.now()): String? {
        val month = date.monthValue
        val day = date.dayOfMonth

        return when {
            month in 2..4 -> "Board exam season hai. Tu kar sakta/sakti hai!"
            month == 3 && day in 24..26 -> "Holi ke baad bhi padhai hoti hai. Wapas aa jao!"
            month == 10 && day in 20..25 -> "Diwali mubarak! Thoda padh lo, phir celebrate karo"
            month == 1 && day == 26 -> "Republic Day! Desh ke liye padho"
            month == 8 && day == 15 -> "Independence Day! Desh ke liye padho"
            month == 9 && day == 5 -> "Teachers' Day! Apne teachers ko yaad karo"
            month == 11 && day == 14 -> "Children's Day! Padhai ka maza lo"
            else -> null
        }
    }
}

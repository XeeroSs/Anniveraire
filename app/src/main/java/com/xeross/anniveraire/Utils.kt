package com.xeross.anniveraire

import android.content.Context
import android.os.Build
import java.time.temporal.ChronoUnit
import java.util.*

object Utils {

    fun getRemainingDays(date: Date, dateToday: Date): Long {
        val dateBefore = date.clone() as Date
        dateBefore.year = dateToday.year
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ChronoUnit.DAYS.between(dateToday.toInstant(),
                    dateBefore.toInstant())
        } else (dateToday.time - dateBefore.time) / (1000 * 60 * 60 * 24)
    }

    fun getDateInString(date: Date, context: Context): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val month = getMonthInString(calendar, context)

        return context.getString(R.string.format_date, day, month, year)
    }

    fun getDateWithoutYearInString(date: Date, context: Context): CharSequence? {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = getMonthInString(calendar, context)

        return context.getString(R.string.format_date_without_year, day, month)
    }

    private fun getMonthInString(calendar: Calendar, context: Context) = when (calendar.get(Calendar.MONTH)) {
        0 -> context.getString(R.string.january)
        1 -> context.getString(R.string.february)
        2 -> context.getString(R.string.march)
        3 -> context.getString(R.string.april)
        4 -> context.getString(R.string.may)
        5 -> context.getString(R.string.june)
        6 -> context.getString(R.string.july)
        7 -> context.getString(R.string.august)
        8 -> context.getString(R.string.september)
        9 -> context.getString(R.string.october)
        10 -> context.getString(R.string.november)
        11 -> context.getString(R.string.december)
        else -> context.getString(R.string.string_problem)
    }
}
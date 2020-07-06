package com.xeross.anniveraire

import android.content.Context
import android.os.Build
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

object UtilsDate {

    fun getRemainingDays(date: Date, dateToday: Date): Long {
        val dateBefore = date.clone() as Date
        dateBefore.year = dateToday.year
        if (dateBefore.month > dateToday.month) dateBefore.year.plus(1)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ChronoUnit.DAYS.between(dateToday.toInstant(),
                    dateBefore.toInstant())
        } else (dateToday.time - dateBefore.time) / (1000 * 60 * 60 * 24)
    }

    fun getStringInDate(dateString: String): Date = SimpleDateFormat("dd/MM/yyyy").parse(dateString)

    fun getDateInString(date: Date): String = SimpleDateFormat("dd/MM/yyyy").format(date)

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

    fun getAgeEvent(date: Date, dateToday: Date): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ChronoUnit.YEARS.between(dateToday.toInstant().atZone(ZoneId.systemDefault()), date.toInstant().atZone(ZoneId.systemDefault())).toInt()
        } else {
            val calendarDate = Calendar.getInstance()
            val calendarNow = Calendar.getInstance()
            calendarDate.time = date
            calendarNow.time = dateToday
            if (calendarNow.after(calendarDate)) return 0
            val year1 = calendarDate.get(Calendar.YEAR)
            val year2 = calendarNow.get(Calendar.YEAR)
            var age = year1 - year2
            val month1 = calendarDate.get(Calendar.MONTH)
            val month2 = calendarNow.get(Calendar.MONTH)
            if (month2 > month1) {
                age--
            } else if (month1 == month2) {
                val day1 = calendarDate.get(Calendar.DAY_OF_MONTH)
                val day2 = calendarNow.get(Calendar.DAY_OF_MONTH)
                if (day2 > day1) {
                    age--
                }
            }
            return age
        }
    }
}
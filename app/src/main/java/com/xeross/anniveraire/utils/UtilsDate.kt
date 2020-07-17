package com.xeross.anniveraire.utils

import android.content.Context
import android.os.Build
import com.xeross.anniveraire.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs

object UtilsDate {

    fun getRemainingDays(date: Date, today: Date): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val birthday: LocalDate = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val dateToday: LocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

            var nextBDay: LocalDate = birthday.withYear(dateToday.year)

            if (nextBDay.isBefore(dateToday) || nextBDay.isEqual(dateToday)) {
                nextBDay = nextBDay.plusYears(1)
            }

            return ChronoUnit.DAYS.between(dateToday, nextBDay).toInt()
        }
        val dateToday = Calendar.getInstance()
        dateToday.timeInMillis = today.time
        dateToday[Calendar.HOUR_OF_DAY] = 0
        dateToday[Calendar.MINUTE] = 0
        dateToday[Calendar.SECOND] = 0
        dateToday[Calendar.MILLISECOND] = 0

        val dateBirthday = Calendar.getInstance()
        dateBirthday.timeInMillis = date.time
        dateBirthday[Calendar.HOUR_OF_DAY] = 0
        dateBirthday[Calendar.YEAR] = dateToday.get(Calendar.YEAR)
        dateBirthday[Calendar.MINUTE] = 0
        dateBirthday[Calendar.SECOND] = 0
        dateBirthday[Calendar.MILLISECOND] = 0

        val monthToday = dateToday.get(Calendar.MONTH)
        val monthBirthday = dateBirthday.get(Calendar.MONTH)
        val dayToday = dateToday.get(Calendar.DAY_OF_MONTH)
        val dayBirthday = dateBirthday.get(Calendar.DAY_OF_MONTH)

        if (monthToday == monthBirthday) {
            if (dayBirthday == dayToday) return 0
            if (dayToday < dayBirthday) {
                dateBirthday[Calendar.YEAR] = dateToday.get(Calendar.YEAR).plus(1)
                dateBirthday[Calendar.DAY_OF_MONTH] = dayToday
                dateToday[Calendar.DAY_OF_MONTH] = dayBirthday
            }
        } else if (monthToday < monthBirthday) {
            dateBirthday[Calendar.YEAR] = dateToday.get(Calendar.YEAR).plus(1)
            dateBirthday[Calendar.MONTH] = monthToday
            dateToday[Calendar.MONTH] = monthBirthday
        }

        val milis1: Long = dateBirthday.timeInMillis
        val milis2: Long = dateToday.timeInMillis

        val diff = abs(milis2 - milis1)

        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }

    fun getStringInDate(dateString: String): Date = SimpleDateFormat("dd/MM/yyyy").parse(dateString)

    fun getDateWithHourInString(date: Date): String =
            SimpleDateFormat("dd/MM/yyyy HH:mm").format(date)

    fun getDateInString(date: Date): String = SimpleDateFormat("dd/MM/yyyy").format(date)

    fun getDateInString(date: Date, context: Context): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val month = getMonthInString(calendar, context)

        return context.getString(R.string.format_date, day, month, year)
    }

    fun getDateWithHourInString(date: Date, context: Context): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val month = getMonthInString(calendar, context)

        return context.getString(R.string.format_date_with_hour, day, month, year, hour, minute)
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
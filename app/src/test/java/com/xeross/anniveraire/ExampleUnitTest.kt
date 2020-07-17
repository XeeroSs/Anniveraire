package com.xeross.anniveraire

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun date_isCorrect() {
        val calendar = Calendar.getInstance()
        calendar.time = Date(120 /*= 2020 (120 + 1900)*/, 1, 11)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val month = when (calendar.get(Calendar.MONTH)) {
            0 -> "january"
            1 -> "february"
            2 -> "march"
            3 -> "april"
            4 -> "may"
            5 -> "june"
            6 -> "july"
            7 -> "august"
            8 -> "september"
            9 -> "october"
            10 -> "november"
            11 -> "december"
            else -> "???"
        }

        assertEquals("11 february 2020", "$day $month $year")
    }

    @Test
    fun remainingDays_isCorrect() {
        val dateBefore = Date(110, 5, 21)
        val dateNow = Date()

        assertEquals("29", "${getRemainingDays(dateBefore, dateNow)}")
    }

    fun getRemainingDays(date: Date, dateToday: Date): Long {
        val dateBefore = date.clone() as Date
        if (dateBefore.month == dateToday.month) {
            println("month == month")
            if (dateBefore.day == dateToday.day) return 0
            if (dateBefore.day < dateToday.day) {
                println("day < day")
                dateBefore.year = dateToday.year
                dateToday.year = dateToday.year.plus(1)
            } else {
                println("day > day")
                dateBefore.year = dateToday.year
            }
        } else if (dateBefore.month < dateToday.month) {
            println("month < month")
            dateBefore.year = dateToday.year
            dateToday.year = dateToday.year.plus(1)

        } else {
            println("month > month")
            dateBefore.year = dateToday.year
        }
        //dateBefore.year = dateToday.year
        return ChronoUnit.DAYS.between(dateBefore.toInstant(),
                dateToday.toInstant())
        /*return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             ChronoUnit.DAYS.between(dateToday.toInstant(),
                     dateBefore.toInstant())
         } else (dateToday.time - dateBefore.time) / (1000 * 60 * 60 * 24)*/
    }

    @Test
    fun remainingDaysUnderAPI26_isCorrect() {
        val remainingDays = (Date(120, 2, 11).time
                - Date(120, 1, 11).time) / (1000 * 60 * 60 * 24)

        assertEquals("29", "$remainingDays")
    }

    @Test
    fun ageEvent_isCorrect() {
        val date = Date()
        val dateToday = Date(100, 7, 11)
        assertEquals(19, getAgeEvent(date, dateToday))
    }

    @Test
    fun ageEventAPI26_isCorrect() {
        val date = Date()
        val dateToday = Date(100, 7, 11)
        assertEquals(19, getAgeEventAPI26(date, dateToday))
    }

    @Test
    fun getRelativeTime_isCorrect() {
        val date = Date(100, 3, 22)
        assertEquals(657, getCountOfDays(date, Date()))
    }

    private fun getCountOfDays(date: Date, today: Date): Long {
        val birthday = LocalDate.parse(SimpleDateFormat("yyyy-MM-dd").format(date))
        val dateToday = LocalDate.parse(SimpleDateFormat("yyyy-MM-dd").format(today))
        var nextBDay: LocalDate = birthday.withYear(dateToday.year)

        if (nextBDay.isBefore(dateToday) || nextBDay.isEqual(dateToday)) {
            nextBDay = nextBDay.plusYears(1)
        }

        return ChronoUnit.DAYS.between(dateToday, nextBDay)
    }

    fun getAgeEvent(date: Date, dateToday: Date): Int? {
        val calendarDate = Calendar.getInstance()
        val calendarNow = Calendar.getInstance()
        calendarDate.time = date
        calendarNow.time = dateToday
        if (calendarNow.after(calendarDate)) return 0
        val year1 = calendarDate.get(Calendar.YEAR);
        val year2 = calendarNow.get(Calendar.YEAR);
        var age = year1 - year2;
        val month1 = calendarDate.get(Calendar.MONTH);
        val month2 = calendarNow.get(Calendar.MONTH);
        if (month2 > month1) {
            age--;
        } else if (month1 == month2) {
            val day1 = calendarDate.get(Calendar.DAY_OF_MONTH);
            val day2 = calendarNow.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }
        return age
    }

    fun getAgeEventAPI26(date: Date, dateToday: Date): Int? {
        return ChronoUnit.YEARS.between(dateToday.toInstant().atZone(ZoneId.systemDefault()), date.toInstant().atZone(ZoneId.systemDefault())).toInt()
    }
}

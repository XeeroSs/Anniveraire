package com.xeross.anniveraire

import org.junit.Assert.assertEquals
import org.junit.Test
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
        val remainingDays = ChronoUnit.DAYS.between(Date(120, 1, 11).toInstant(),
                Date(120, 2, 11).toInstant())

        assertEquals("29", "$remainingDays")
    }

    @Test
    fun remainingDaysUnderAPI26_isCorrect() {
        val remainingDays = (Date(120, 2, 11).time
                - Date(120, 1, 11).time) / (1000 * 60 * 60 * 24)

        assertEquals("29", "$remainingDays")
    }
}

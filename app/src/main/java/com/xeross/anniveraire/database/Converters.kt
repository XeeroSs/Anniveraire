package com.xeross.anniveraire.database

import androidx.room.TypeConverter
import com.xeross.anniveraire.model.BirthdayState
import java.util.*

class Converters {
    @TypeConverter
    fun dateFromTimestamp(value: Long?) = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?) = date?.time

    @TypeConverter
    fun enumFromName(name: String?) = name?.let { BirthdayState.valueOf(name) }

    @TypeConverter
    fun enumToName(state: BirthdayState?) = state?.name
}

package com.xeross.anniveraire.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xeross.anniveraire.database.dao.BirthdayDAO
import com.xeross.anniveraire.model.Birthday

@Database(entities = [Birthday::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BirthdayDatabase : RoomDatabase() {

    abstract fun birthdayDAO(): BirthdayDAO

    companion object {

        // Singleton
        @Volatile
        private var INSTANCE: BirthdayDatabase? = null

        // Instance
        fun getInstance(context: Context): BirthdayDatabase? {
            INSTANCE?.let { return it } ?: synchronized(BirthdayDatabase::class.java) {
                INSTANCE?.let { return it }
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                        BirthdayDatabase::class.java, "birthdays.db")
                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE
        }
    }
}
package com.xeross.anniveraire.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xeross.anniveraire.model.Birthday.Companion.TABLE_NAME
import java.util.*

@Entity(tableName = TABLE_NAME)
data class Birthday(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                    var firstName: String = "",
                    var lastName: String = "",
                    var dateBirth: Date = Date(),
                    var imageURL: String = "",
                    var state: BirthdayState = BirthdayState.BIRTHDAY) {
    companion object {
        const val TABLE_NAME: String = "birthdays"
    }
}
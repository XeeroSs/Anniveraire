package com.xeross.anniveraire.database.dao

import androidx.room.*
import com.xeross.anniveraire.model.Birthday
import com.xeross.anniveraire.model.Birthday.Companion.TABLE_NAME

@Dao
interface BirthdayDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createBirthday(birthday: Birthday)

    @Update
    fun updateBirthday(birthday: Birthday)

    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
    fun deleteBirthday(id: Int)

    @Query("SELECT * FROM $TABLE_NAME")
    fun getBirthdays(): List<Birthday>

    /* @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
     fun getBirthday(id: Int): Birthday*/
}

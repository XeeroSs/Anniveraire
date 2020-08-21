package com.xeross.anniveraire.database

import com.xeross.anniveraire.database.dao.BirthdayDAO
import com.xeross.anniveraire.model.Birthday

class BirthdayDataRepository(private val birthdayDao: BirthdayDAO?) {

    // GET
    fun getBirthday(id: Int) = birthdayDao?.getBirthday(id)

    // GET ALL
    fun getBirthdays() = birthdayDao?.getBirthdays()

    // DELETE
    fun deleteBirthday(id: Int) = birthdayDao?.deleteBirthday(id)

    // CREATE
    fun createBirthday(birthday: Birthday) = birthdayDao?.createBirthday(birthday)

    // UPDATE
    fun updateBirthday(birthday: Birthday) = birthdayDao?.updateBirthday(birthday)

}

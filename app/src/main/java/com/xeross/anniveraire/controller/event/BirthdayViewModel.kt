package com.xeross.anniveraire.controller.event

import androidx.lifecycle.ViewModel
import com.xeross.anniveraire.database.BirthdayDataRepository
import com.xeross.anniveraire.model.Birthday
import java.util.concurrent.Executor

class BirthdayViewModel(private val birthdayDataRepository: BirthdayDataRepository?,
                        private val executor: Executor
        /*private val context: Context*/) : ViewModel() {

    fun deleteBirthday(id: Int) = birthdayDataRepository?.deleteBirthday(id)

    fun getBirthday(id: Int) = birthdayDataRepository?.getBirthday(id)
    fun getBirthdays() = birthdayDataRepository?.getBirthdays()

    // CREATE PROPERTY
    fun createBirthday(birthday: Birthday) = executor.execute {
        birthdayDataRepository?.createBirthday(birthday)
    }

    fun updateBirthday(birthday: Birthday) = executor.execute {
        birthdayDataRepository?.updateBirthday(birthday)
    }
}
package com.xeross.anniveraire.controller.event

import android.content.Context
import com.xeross.anniveraire.database.BirthdayDatabase
import com.xeross.anniveraire.model.Birthday

class BirthdayPresenter(context: Context, private val contract: BirthdayContract.View) : BirthdayContract.Presenter {

    // private val executor = Executors.newSingleThreadExecutor()
    private val database = BirthdayDatabase.getInstance(context)
    private val birthdayDAO = database?.birthdayDAO()

    override fun getBirthdays() {
        birthdayDAO?.getBirthdays()?.let { contract.getBirthdays(it) }
    }

    override fun addBirthday(birthday: Birthday) {
        //      executor.execute {
        birthdayDAO?.createBirthday(birthday)
        contract.getBirthdays()
        //    }
    }

    override fun deleteBirthday(id: Int) {
        birthdayDAO?.deleteBirthday(id)
    }

    override fun updateBirthday(birthday: Birthday) {
        //  executor.execute {
        birthdayDAO?.updateBirthday(birthday)
        contract.getBirthdays()
        //}
    }
}
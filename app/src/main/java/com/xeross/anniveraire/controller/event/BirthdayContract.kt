package com.xeross.anniveraire.controller.event

import com.xeross.anniveraire.model.Birthday
import com.xeross.anniveraire.model.Gallery

interface BirthdayContract {
    interface View {
        fun removeBirthdays()
        fun getBirthdays(tObjects: List<Birthday>)
        fun getBirthdays()
    }

    interface Presenter {
        fun getBirthdays()
        fun addBirthday(birthday: Birthday)
        fun deleteBirthday(id: Int)
        fun updateBirthday(birthday: Birthday)
    }
}
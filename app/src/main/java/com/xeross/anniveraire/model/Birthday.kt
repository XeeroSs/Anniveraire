package com.xeross.anniveraire.model

import java.util.*

data class Birthday(
    var firstName: String = "",
    var lastName: String = "",
    var dateBirth: Date = Date(),
    var imageURL: String = "",
    var state: BirthdayState = BirthdayState.BIRTHDAY
)
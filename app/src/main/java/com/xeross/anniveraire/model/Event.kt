package com.xeross.anniveraire.model

import java.util.*

data class Event(
    var firstName: String,
    var lastName: String,
    var age: Date,
    var imageURL: String,
    var isBirthday: Boolean = true
) {

    constructor(
        firstName: String,
        age: Date, imageURL: String
    ) :
            this(firstName, "", age, imageURL, false)

}
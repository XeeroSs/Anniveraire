package com.xeross.anniveraire.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class Message(val urlImage: String? = null, val userSender: User? = null, val message: String? = null) {

    private val dateCreated: Date? = null

    @ServerTimestamp
    fun getDateCreated(): Date? {
        return dateCreated
    }
}
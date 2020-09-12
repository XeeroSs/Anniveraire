package com.xeross.anniveraire.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class Message(val userSender: User?,
                   val urlImage: String?, val message: String?) {

    private val dateCreated: Date? = null

    @ServerTimestamp
    fun getDateCreated(): Date? {
        return dateCreated
    }

}
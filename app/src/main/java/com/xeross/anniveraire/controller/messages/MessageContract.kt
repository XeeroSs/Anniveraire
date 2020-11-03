package com.xeross.anniveraire.controller.messages

import com.google.firebase.firestore.Query
import com.xeross.anniveraire.model.User

interface MessageContract {
    interface View {
        fun getMessages(query: Query)
        fun getUser(user:User)
    }

    interface Presenter {
        fun getUser(userId:String)
        fun getMessages(chat: String)
        fun createMessageWithImage(urlImage: String, message: String, discussionId: String, user: User)
        fun createMessage(message: String, discussionId: String, user: User)
    }
}
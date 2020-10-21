package com.xeross.anniveraire.controller.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xeross.anniveraire.model.Message
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.DISCUSSION_COLLECTION
import com.xeross.anniveraire.utils.Constants.MESSAGE_COLLECTION
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION
import java.util.*

class MessageViewModel : ViewModel() {

    private val databaseMessageInstance =
            FirebaseFirestore.getInstance().collection(MESSAGE_COLLECTION)
    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
    private val databaseDiscussionInstance =
            FirebaseFirestore.getInstance().collection(DISCUSSION_COLLECTION)

    fun getAllMessageForChat(chat: String): Query {
        return databaseMessageInstance.document(chat)
                .collection(MESSAGE_COLLECTION).orderBy("dateCreated").limit(50)
    }

    private fun getDocumentUser(id: String) = databaseUsersInstance.document(id).get()

    fun createMessageForChat(message: String, discussionId: String, user: User) {
        databaseMessageInstance.document(discussionId).collection(MESSAGE_COLLECTION).add(Message(userSender = user, message = message))
        updateDateDiscussion(discussionId)
    }

    fun getUser(userId: String): LiveData<User> {
        val mutableLiveData = MutableLiveData<User>()
        getDocumentUser(userId).addOnCompleteListener { t ->
            t.result?.toObject(User::class.java)?.let { user ->
                mutableLiveData.postValue(user)
            }
        }
        return mutableLiveData
    }

    fun createMessageForChat(urlImage: String, message: String, discussionId: String, user: User) {
        databaseMessageInstance.document(discussionId).collection(MESSAGE_COLLECTION).add(Message(urlImage, user, message))
        updateDateDiscussion(discussionId)
    }

    private fun updateDateDiscussion(discussionId: String) {
        databaseDiscussionInstance.document(discussionId).update("activityDate", Calendar.getInstance().time)
    }
}
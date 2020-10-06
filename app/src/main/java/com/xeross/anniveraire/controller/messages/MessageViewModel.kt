package com.xeross.anniveraire.controller.messages

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.Message
import com.xeross.anniveraire.model.User
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

class MessageViewModel(private val executor: Executor) : ViewModel() {

    companion object {
        const val MESSAGE_COLLECTION = "messages"
        const val USERS_COLLECTION = "users"
        const val DISCUSSION_COLLECTION = "discussions"
    }

    private val databaseMessageInstance =
            FirebaseFirestore.getInstance().collection(MESSAGE_COLLECTION)
    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
    private val databaseDiscussionInstance =
            FirebaseFirestore.getInstance().collection(DISCUSSION_COLLECTION)

    fun getAllMessageForChat(chat: String): Query {
        return databaseMessageInstance.document(chat).collection(MESSAGE_COLLECTION).orderBy("dateCreated")
                .limit(50)
    }

    fun getUser(id: String) = databaseUsersInstance.document(id).get()
    fun getDiscussion(id: String) = databaseDiscussionInstance.document(id).get()

    fun getUsers() = databaseUsersInstance
    fun getDiscussions(): LiveData<List<Discussion>>? {
        databaseMessageInstance.get().addOnCompleteListener { task ->
            task.result?.let { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    document.toObject(Discussion::class.java)?.let { discussion -> }
                }
            }
        }
        return null
    }

    fun createDiscussion(discussion: Discussion, context: Context) = executor.execute {
        databaseMessageInstance.document().set(discussion).addOnCompleteListener {
        }.addOnFailureListener {
        }
    }

    fun createMessageForChat(message: String, discussionId: String, user: User) {
        databaseMessageInstance.document(discussionId).collection(MESSAGE_COLLECTION).add(Message(userSender = user, message = message))
        updateDateDiscussion(discussionId)
    }

    fun createMessageForChat(urlImage: String, message: String, discussionId: String, user: User) {
        databaseMessageInstance.document(discussionId).collection(MESSAGE_COLLECTION).add(Message(urlImage, user, message))
        updateDateDiscussion(discussionId)
    }

    private fun updateDateDiscussion(discussionId: String) {
        databaseDiscussionInstance.document(discussionId).update("activityDate", Calendar.getInstance().time)
    }

    fun updateDiscussionsRequestUser(id: String, discussionsRequestId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("discussionsRequestId", discussionsRequestId)
    }
}
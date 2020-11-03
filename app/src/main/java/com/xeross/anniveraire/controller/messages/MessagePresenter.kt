package com.xeross.anniveraire.controller.messages

import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Message
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.DISCUSSION_COLLECTION
import com.xeross.anniveraire.utils.Constants.MESSAGE_COLLECTION
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION
import java.util.*

class MessagePresenter(private val contract: MessageContract.View) : MessageContract.Presenter {

    private val databaseMessageInstance =
            FirebaseFirestore.getInstance().collection(MESSAGE_COLLECTION)
    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
    private val databaseDiscussionInstance =
            FirebaseFirestore.getInstance().collection(DISCUSSION_COLLECTION)

    override fun getMessages(chat: String) {
        val query = databaseMessageInstance.document(chat)
                .collection(MESSAGE_COLLECTION).orderBy("dateCreated").limit(50)
        contract.getMessages(query)
    }

    private fun getDocumentUser(id: String) = databaseUsersInstance.document(id).get()

    override fun createMessage(message: String, discussionId: String, user: User) {
        databaseMessageInstance.document(discussionId).collection(MESSAGE_COLLECTION).add(Message(userSender = user, message = message))
        updateDateDiscussion(discussionId)
    }

    override fun getUser(userId: String) {
        getDocumentUser(userId).addOnCompleteListener { t ->
            t.result?.toObject(User::class.java)?.let { user ->
                contract.getUser(user)
            }
        }
    }

    override fun createMessageWithImage(urlImage: String, message: String, discussionId: String, user: User) {
        databaseMessageInstance.document(discussionId).collection(MESSAGE_COLLECTION).add(Message(urlImage, user, message))
        updateDateDiscussion(discussionId)
    }

    private fun updateDateDiscussion(discussionId: String) {
        databaseDiscussionInstance.document(discussionId).update("activityDate", Calendar.getInstance().time)
    }
}
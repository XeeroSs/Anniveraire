package com.xeross.anniveraire.controller.discussion.user

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class DiscussionUserViewModel : ViewModel() {

    companion object {
        const val USERS_COLLECTION = "users"
        const val DISCUSSION_COLLECTION = "discussions"
    }

    private val databaseDiscussionInstance =
            FirebaseFirestore.getInstance().collection(DISCUSSION_COLLECTION)
    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    fun getUsers() = databaseUsersInstance
    fun getUser(id: String) = databaseUsersInstance.document(id).get()
    fun getDiscussion(id: String) = databaseDiscussionInstance.document(id).get()

    fun updateDiscussionsUser(id: String, discussionsId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("discussionsId", discussionsId)
    }
}

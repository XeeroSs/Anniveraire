package com.xeross.anniveraire.controller.discussion.request

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Discussion
import java.util.concurrent.Executor

class DiscussionRequestViewModel(private val executor: Executor) : ViewModel() {

    companion object {
        const val DISCUSSION_COLLECTION = "discussions"
        const val USERS_COLLECTION = "users"
    }

    private val databaseInstanceDiscussion =
            FirebaseFirestore.getInstance().collection(DISCUSSION_COLLECTION)
    private val databaseInstanceUsers =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    fun getDiscussions(discussionId: String) = databaseInstanceDiscussion.document(discussionId).get()
    fun getUser(userId: String) = databaseInstanceUsers.document(userId).get()

    private fun updateCountDiscussionsUser(id: String, discussionsId: ArrayList<String>) {
        databaseInstanceUsers.document(id).update("discussionsId", discussionsId)
    }

    private fun updateCountUserDiscussionsId(id: String, usersId: ArrayList<String>) {
        databaseInstanceDiscussion.document(id).update("usersId", usersId)
    }

    fun updateDiscussionAndUser(discussion: Discussion, userId: String, discussionsId: ArrayList<String>?) = executor.execute {
        if (discussion.usersId.contains(userId)) return@execute
        if (discussionsId == null) return@execute
        if (discussionsId.contains(discussion.id)) return@execute
        discussion.usersId.add(userId)
        discussionsId.add(discussion.id)
        updateCountDiscussionsUser(userId, discussionsId)
        updateCountUserDiscussionsId(discussion.id, discussion.usersId)
    }

    fun discussionRequestRemove(discussion: Discussion, userId: String, discussionsRequestId: ArrayList<String>?) {
        discussionsRequestId?.remove(discussion.id)
        discussionsRequestId?.let { updateCountDiscussionRequest(userId, it) }
    }

    private fun updateCountDiscussionRequest(id: String, discussionsRequestId: ArrayList<String>) {
        databaseInstanceUsers.document(id).update("discussionsRequestId", discussionsRequestId)
    }
}
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

    /*fun getDiscussions(): LiveData<List<Discussion>>? {
        databaseInstance.get().addOnCompleteListener { task ->
            task.result?.let { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    document.toObject(Discussion::class.java)?.let { discussion -> }
                }
            }
        }
        return null
    }*/

    private fun updateCountDiscussionsUser(id: String, discussionsId: ArrayList<String>) {
        databaseInstanceUsers.document(id).update("discussionsId", discussionsId)
    }
    private fun updateCountDiscussionsId(id: String, discussionsId: ArrayList<String>) {
        databaseInstanceDiscussion.document(id).update("userId", discussionsId)
    }

    fun updateDiscussionAndUser(discussion: Discussion, userId: String, discussionsId: ArrayList<String>?) = executor.execute {
        discussion.usersId.add(userId)
        discussionsId?.add(discussion.id)
        discussionsId?.let { updateCountDiscussionsUser(userId, it) }
        updateCountDiscussionsId(discussion.id, discussion.usersId)
    }

    fun discussionDeny(discussion: Discussion, userId: String, discussionsRequestId: ArrayList<String>?) {
        discussionsRequestId?.remove(discussion.id)
        discussionsRequestId?.let { updateCountDiscussionsUser(userId, it) }
    }
}
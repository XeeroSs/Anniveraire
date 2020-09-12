package com.xeross.anniveraire.controller.discussion

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Discussion
import java.util.concurrent.Executor

class DiscussionViewModel(private val executor: Executor) : ViewModel() {

    companion object {
        const val DISCUSSION_COLLECTION = "discussions"
        const val USERS_COLLECTION = "users"
    }

    private val databaseInstanceDiscussion =
            FirebaseFirestore.getInstance().collection(DISCUSSION_COLLECTION)
    private val databaseInstanceUsers =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    fun getDiscussions(discussionId: Int) = databaseInstanceDiscussion.document(discussionId.toString()).get()
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

    fun createDiscussion(discussion: Discussion, userId: String) = executor.execute {
        discussion.usersId.add(userId)
        databaseInstanceDiscussion.document().set(discussion).addOnCompleteListener {
        }.addOnFailureListener {
        }
    }
}
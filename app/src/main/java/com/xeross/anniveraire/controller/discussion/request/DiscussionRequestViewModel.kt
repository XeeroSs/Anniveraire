package com.xeross.anniveraire.controller.discussion.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.User

class DiscussionRequestViewModel : ViewModel() {

    companion object {
        const val DISCUSSION_COLLECTION = "discussions"
        const val USERS_COLLECTION = "users"
    }

    private val databaseInstanceDiscussion =
            FirebaseFirestore.getInstance().collection(DISCUSSION_COLLECTION)
    private val databaseInstanceUsers =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    private fun getDocumentDiscussion(discussionId: String) = databaseInstanceDiscussion.document(discussionId).get()
    private fun getDocumentUser(userId: String) = databaseInstanceUsers.document(userId).get()

    private fun updateDiscussionIdsFromUser(id: String, discussionsId: ArrayList<String>) {
        databaseInstanceUsers.document(id).update("discussionsId", discussionsId)
    }

    private fun updateUserIdsFromDiscussion(id: String, usersId: ArrayList<String>) {
        databaseInstanceDiscussion.document(id).update("usersId", usersId)
    }

    private fun updateDiscussionRequestFromUser(id: String, discussionsRequestId: ArrayList<String>) {
        databaseInstanceUsers.document(id).update("discussionsRequestId", discussionsRequestId)
    }

    fun getDiscussionsFromUser(userId: String): LiveData<ArrayList<Discussion>> {
        val mutableLiveData = MutableLiveData<ArrayList<Discussion>>()
        val galleries = ArrayList<Discussion>()
        getDocumentUser(userId).addOnCompleteListener { taskUser ->
            taskUser.result?.toObject(User::class.java)?.let { user ->
                user.discussionsRequestId.forEach { dId ->
                    getDocumentDiscussion(dId).addOnCompleteListener { taskDiscussion ->
                        taskDiscussion.result?.toObject(Discussion::class.java)?.let { discussion ->
                            galleries.add(discussion)
                            mutableLiveData.postValue(galleries)
                        }
                    }
                }
            }
        }
        return mutableLiveData
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

    fun joinDiscussion(discussion: Discussion, userId: String, discussionsId: ArrayList<String>?) {
        if (discussion.usersId.contains(userId)) return
        if (discussionsId == null) return
        if (discussionsId.contains(discussion.id)) return
        discussion.usersId.add(userId)
        discussionsId.add(discussion.id)
        updateDiscussionIdsFromUser(userId, discussionsId)
        updateUserIdsFromDiscussion(discussion.id, discussion.usersId)
    }

    fun removeDiscussionRequest(discussion: Discussion, userId: String, discussionsRequestId: ArrayList<String>?) {
        discussionsRequestId?.remove(discussion.id)
        discussionsRequestId?.let { updateDiscussionRequestFromUser(userId, it) }
    }
}
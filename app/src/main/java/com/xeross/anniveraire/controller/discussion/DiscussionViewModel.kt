package com.xeross.anniveraire.controller.discussion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.DISCUSSION_COLLECTION
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION

class DiscussionViewModel : ViewModel() {

    private val databaseInstanceDiscussion =
            FirebaseFirestore.getInstance().collection(DISCUSSION_COLLECTION)
    private val databaseInstanceUsers =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    private fun getDocumentDiscussion(discussionId: String) = databaseInstanceDiscussion.document(discussionId).get()
    private fun getDocumentUser(userId: String) = databaseInstanceUsers.document(userId).get()

    private fun updateDiscussionIdsFromUser(id: String, discussionsId: ArrayList<String>?) {
        databaseInstanceUsers.document(id).update("discussionsId", discussionsId)
    }

    fun deleteDiscussion(discussionId: String) {
        databaseInstanceDiscussion.document(discussionId).delete()
    }

    private fun updateUserIdsFromDiscussions(id: String, userIds: ArrayList<String>) {
        databaseInstanceDiscussion.document(id).update("usersId", userIds)
    }

    fun getDiscussionsFromUser(userId: String): LiveData<ArrayList<Discussion>> {
        val mutableLiveData = MutableLiveData<ArrayList<Discussion>>()
        getDocumentUser(userId).addOnCompleteListener { taskUser ->
            taskUser.result?.toObject(User::class.java)?.let { user ->
                val discussions = ArrayList<Discussion>()
                user.discussionsId.forEach { dId ->
                    getDocumentDiscussion(dId).addOnCompleteListener { taskDiscussion ->
                        taskDiscussion.result?.toObject(Discussion::class.java)?.let { discussion ->
                            if (!discussions.contains(discussion)) {
                                discussions.add(discussion)
                                mutableLiveData.postValue(discussions)
                            }
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

    fun getDiscussion(discussionId: String): LiveData<Discussion> {
        val mutableLiveData = MutableLiveData<Discussion>()
        getDocumentDiscussion(discussionId).addOnCompleteListener { t ->
            t.result?.toObject(Discussion::class.java)?.let { discussion ->
                mutableLiveData.postValue(discussion)
            }
        }
        return mutableLiveData
    }

    fun removeDiscussionFromUser(discussion: Discussion, userId: String, discussionsId: ArrayList<String>?) {
        discussion.usersId.remove(userId)
        discussionsId?.remove(discussion.id)
        discussionsId?.let { updateDiscussionIdsFromUser(userId, it) }
        updateUserIdsFromDiscussions(discussion.id, discussion.usersId)
    }

    fun createDiscussion(discussion: Discussion, userId: String, discussionsId: ArrayList<String>?) {
        discussion.usersId.add(userId)
        discussionsId?.add(discussion.id)
        updateDiscussionIdsFromUser(userId, discussionsId)
        databaseInstanceDiscussion.document(discussion.id).set(discussion).addOnCompleteListener {
        }
    }

    fun updateDiscussionName(name: String, discussionId: String) {
        databaseInstanceDiscussion.document(discussionId).update("name", name)
    }
}
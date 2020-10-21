package com.xeross.anniveraire.controller.discussion.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.DISCUSSION_COLLECTION
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION

class DiscussionUserViewModel : ViewModel() {

    private val databaseDiscussionInstance =
            FirebaseFirestore.getInstance().collection(DISCUSSION_COLLECTION)
    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    fun getUsers() = databaseUsersInstance
    private fun getDocumentUser(id: String) = databaseUsersInstance.document(id).get()
    private fun getDocumentDiscussion(id: String) = databaseDiscussionInstance.document(id).get()

    fun getUserFromDiscussion(discussionId: String): LiveData<ArrayList<User>> {
        val mutableLiveData = MutableLiveData<ArrayList<User>>()
        getDocumentDiscussion(discussionId).addOnCompleteListener { taskDiscussion ->
            taskDiscussion.result?.toObject(Discussion::class.java)?.let { discussion ->
                val users = ArrayList<User>()
                discussion.usersId.forEach { uId ->
                    getDocumentUser(uId).addOnCompleteListener { taskUser ->
                        taskUser.result?.toObject(User::class.java)?.let { user ->
                            if (!users.contains(user)) {
                                users.add(user)
                                mutableLiveData.postValue(users)
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

    fun updateDiscussionIdsFromUser(id: String, discussionsId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("discussionsId", discussionsId)
    }

    fun updateUserIdsFromDiscussion(id: String, usersId: ArrayList<String>) {
        databaseDiscussionInstance.document(id).update("usersId", usersId)
    }

    fun updateDiscussionsRequestUser(id: String, discussionsRequestId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("discussionsRequestId", discussionsRequestId)
    }
}

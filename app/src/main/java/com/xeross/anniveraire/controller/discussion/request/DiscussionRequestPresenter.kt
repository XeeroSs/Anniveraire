package com.xeross.anniveraire.controller.discussion.request

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.listener.RequestContract
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.DISCUSSION_COLLECTION
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION

class DiscussionRequestPresenter(private val contract: RequestContract.View<Discussion>) : ViewModel(),
        RequestContract.Presenter<Discussion> {

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

    override fun getObjectsFromUser(userId: String) {
        contract.setList()
        val discussions = ArrayList<Discussion>()
        getDocumentUser(userId).addOnCompleteListener { taskUser ->
            taskUser.result?.toObject(User::class.java)?.let { user ->
                user.discussionsRequestId.forEach { dId ->
                    getDocumentDiscussion(dId).addOnCompleteListener { taskDiscussion ->
                        taskDiscussion.result?.toObject(Discussion::class.java)?.let { discussion ->
                            if (!discussions.contains(discussion)) {
                                discussions.add(discussion)
                                contract.getObjectsFromUser(discussions)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun joinObject(tObject: Discussion, userId: String) {
        contract.setList()
        getDocumentUser(userId).addOnCompleteListener { t ->
            t.result?.toObject(User::class.java)?.let { user ->
                user.discussionsId.let { discussionsId ->
                    val userIds = tObject.usersId
                    if (userIds.contains(userId)) return@addOnCompleteListener
                    val discussionId = tObject.id
                    if (discussionsId.contains(discussionId)) return@addOnCompleteListener
                    userIds.add(userId)
                    discussionsId.add(discussionId)
                    updateDiscussionIdsFromUser(userId, discussionsId)
                    updateUserIdsFromDiscussion(discussionId, userIds)
                    removeRequest(user.discussionsRequestId, tObject, userId)
                }
            }
        }
    }

    override fun removeObjectRequest(tObject: Discussion, userId: String) {
        contract.setList()
        getDocumentUser(userId).addOnCompleteListener { t ->
            t.result?.toObject(User::class.java)?.let { user ->
                removeRequest(user.discussionsRequestId, tObject, userId)
            }
        }
    }

    private fun removeRequest(rDiscussion: ArrayList<String>, tObject: Discussion, userId: String) {
        rDiscussion.remove(tObject.id)
        updateDiscussionRequestFromUser(userId, rDiscussion)
        contract.getRequests()
    }
}
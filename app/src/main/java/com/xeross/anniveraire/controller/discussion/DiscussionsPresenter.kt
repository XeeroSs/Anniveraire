package com.xeross.anniveraire.controller.discussion

import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.R
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants
import com.xeross.anniveraire.utils.sortDiscussionsByDate

class DiscussionsPresenter(private val contract: DiscussionsContract.View) : DiscussionsContract.Presenter {

    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
    private val databaseDiscussionInstance =
            FirebaseFirestore.getInstance().collection(Constants.DISCUSSION_COLLECTION)

    private fun updateDiscussionIdsFromUser(id: String, discussionsId: ArrayList<String>?) {
        databaseUsersInstance.document(id).update("discussionsId", discussionsId)
    }

    private fun getDocumentUser(id: String) = databaseUsersInstance.document(id).get()
    private fun getDocumentDiscussion(id: String) = databaseDiscussionInstance.document(id).get()

    private fun updateUserIdsFromGallery(id: String, galleriesId: ArrayList<String>) {
        databaseDiscussionInstance.document(id).update("usersId", galleriesId)
    }

    override fun getDiscussions(userId: String) {
        contract.removeDiscussions()
        val discussions = ArrayList<Discussion>()
        getDocumentUser(userId).addOnCompleteListener { taskUser ->
            taskUser.result?.toObject(User::class.java)?.let { user ->
                user.discussionsId.forEach { gId ->
                    getDocumentDiscussion(gId).addOnCompleteListener { taskGallery ->
                        taskGallery.result?.toObject(Discussion::class.java)?.let { discussion ->
                            if (!discussions.contains(discussion)) {
                                discussions.add(discussion)
                                discussions.sortDiscussionsByDate()
                                contract.getDiscussions(discussions)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun addDiscussion(discussion: Discussion, userId: String) {
        contract.removeDiscussions()
        getDocumentUser(userId).addOnCompleteListener { t ->
            t.result?.toObject(User::class.java)?.let { user ->
                createDiscussion(discussion, userId, user.discussionsId)
                contract.sendToast(R.string.discussion_create)
                contract.getDiscussions()
            }
        }
    }

    private fun createDiscussion(discussion: Discussion, userId: String, discussionsId: ArrayList<String>?) {
        discussion.usersId.add(userId)
        discussionsId?.add(discussion.id)
        updateDiscussionIdsFromUser(userId, discussionsId)
        databaseDiscussionInstance.document(discussion.id).set(discussion).addOnCompleteListener {
        }
    }

    override fun deleteDiscussion(id: String, userId: String) {
        databaseDiscussionInstance.document(id).delete()
        contract.getDiscussions()
    }

    override fun leaveDiscussion(id: String, userId: String) {
        contract.removeDiscussions()
        getDocumentDiscussion(id).addOnCompleteListener { gT ->
            gT.result?.toObject(Discussion::class.java)?.let { discussion ->
                getDocumentUser(userId).addOnCompleteListener { uT ->
                    uT.result?.toObject(User::class.java)?.let { user ->
                        val discussionsId = user.discussionsId
                        removeDiscussionFromUser(discussion, userId, discussionsId)
                        contract.getDiscussions()
                    }
                }
            }
        }
    }

    private fun removeDiscussionFromUser(discussion: Discussion, userId: String, discussionsId: ArrayList<String>?) {
        discussion.usersId.remove(userId)
        discussionsId?.remove(discussion.id)
        discussionsId?.let { updateDiscussionIdsFromUser(userId, it) }
        updateUserIdsFromGallery(discussion.id, discussion.usersId)
    }

    override fun updateDiscussionName(id: String, newName: String) {
        contract.removeDiscussions()
        databaseDiscussionInstance.document(id).update("name", newName)
        contract.sendToast(R.string.name_update)
        contract.getDiscussions()
    }
}
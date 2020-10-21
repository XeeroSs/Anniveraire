package com.xeross.anniveraire.controller.login

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION


object LoginHelper {

    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    // Creating a user in Firebase
    fun createUser(uid: String, email: String?,
                   username: String?,
                   urlPicture: String?,
                   discussionId: ArrayList<String>, discussionRequestId: ArrayList<String>, galleriesId: ArrayList<String>, galleriesRequestId: ArrayList<String>): Task<Void> {
        val userToCreate = User(uid, email, username, urlPicture, discussionId, discussionRequestId, galleriesId, galleriesRequestId)
        return databaseUsersInstance.document(uid).set(userToCreate)
    }

    fun getUser(id: String) = databaseUsersInstance.document(id).get()
}
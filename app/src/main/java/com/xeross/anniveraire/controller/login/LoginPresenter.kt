package com.xeross.anniveraire.controller.login

import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION


class LoginPresenter(private val contract: LoginContract.View) : LoginContract.Presenter {

    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    // Creating a user in Firebase
    override fun createUser(uid: String, email: String?, username: String?, urlPicture: String?, discussionId: ArrayList<String>, discussionRequestId: ArrayList<String>, galleriesId: ArrayList<String>, galleriesRequestId: ArrayList<String>) {
        val userToCreate = User(uid, email, username, urlPicture, discussionId, discussionRequestId, galleriesId, galleriesRequestId)
        databaseUsersInstance.document(uid).set(userToCreate)
    }

    override fun getUser(userId: String) {
        databaseUsersInstance.document(userId).get().addOnCompleteListener {
            it.result?.toObject(User::class.java)?.let { user ->
                contract.getUser(user)
            } ?: contract.getUser(null)
        }
    }
}
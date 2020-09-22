package com.xeross.anniveraire.controller.login

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.User
import java.util.concurrent.Executor


class LoginViewModel(private val executor: Executor) : ViewModel() {

    companion object {
        const val USERS_COLLECTION = "users"
    }

    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    // Creating a user in Firebase
    fun createUser(uid: String, email: String?,
                   username: String?,
                   urlPicture: String?,
                   discussionId: ArrayList<String>, discussionRequestId: ArrayList<String>): Task<Void> {
        val userToCreate = User(uid, email, username, urlPicture, discussionId, discussionRequestId)
        return databaseUsersInstance.document(uid).set(userToCreate)
    }

    fun getUser(id: String) = databaseUsersInstance.document(id).get()
}
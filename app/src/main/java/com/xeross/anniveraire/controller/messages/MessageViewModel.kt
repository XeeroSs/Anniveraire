package com.xeross.anniveraire.controller.messages

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xeross.anniveraire.model.Discussion
import java.util.concurrent.Executor

class MessageViewModel(private val executor: Executor) : ViewModel() {

    companion object {
        const val MESSAGE_COLLECTION = "messages"
        const val USERS_COLLECTION = "users"
    }

    private val databaseMessageInstance =
            FirebaseFirestore.getInstance().collection(MESSAGE_COLLECTION)
    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    fun getAllMessageForChat(chat: String): Query {
        return databaseMessageInstance.document(chat).collection(MESSAGE_COLLECTION).orderBy("dateCreated")
                .limit(50)
    }

    fun getUser(id: String) = databaseUsersInstance.document(id).get()

    fun getDiscussions(): LiveData<List<Discussion>>? {
        databaseMessageInstance.get().addOnCompleteListener { task ->
            task.result?.let { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    document.toObject(Discussion::class.java)?.let { discussion -> }
                }
            }
        }
        return null
    }

    fun createDiscussion(discussion: Discussion, context: Context) = executor.execute {
        databaseMessageInstance.document().set(discussion).addOnCompleteListener {
        }.addOnFailureListener {
        }
    }
}
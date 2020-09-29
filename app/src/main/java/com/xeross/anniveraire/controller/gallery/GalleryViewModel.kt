package com.xeross.anniveraire.controller.gallery

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executor

class GalleryViewModel(private val executor: Executor) : ViewModel() {

    companion object {
        const val USERS_COLLECTION = "users"
        const val GALLERY_COLLECTION = "galleries"
    }

    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
    private val databaseGalleryInstance =
            FirebaseFirestore.getInstance().collection(GALLERY_COLLECTION)

    fun getUsers() = databaseUsersInstance
    fun getUser(id: String) = databaseUsersInstance.document(id).get()
    fun getGallery(id: String) = databaseGalleryInstance.document(id).get()

    fun updateGalleriesRequestUser(id: String, galleriesRequestId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("galleriesRequestId", galleriesRequestId)
    }
    fun updateGalleries(id: String, imagesId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("imagesId", imagesId)
    }
}
package com.xeross.anniveraire.controller.gallery.user

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class GalleryUserViewModel : ViewModel() {

    companion object {
        const val USERS_COLLECTION = "users"
        const val GALLERY_COLLECTION = "galleries"
    }

    private val databaseGalleryInstance =
            FirebaseFirestore.getInstance().collection(GALLERY_COLLECTION)
    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    fun getUser(id: String) = databaseUsersInstance.document(id).get()
    fun getGallery(id: String) = databaseGalleryInstance.document(id).get()
    fun getUsers() = databaseUsersInstance

    fun updateGalleryUser(id: String, galleriesId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("galleriesId", galleriesId)
    }
    fun updateGalleriesRequestUser(id: String, galleriesRequestId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("galleriesRequestId", galleriesRequestId)
    }
}

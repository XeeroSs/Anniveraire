package com.xeross.anniveraire.controller.gallery

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

    fun updateGalleryUser(id: String, galleriesId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("galleriesId", galleriesId)
    }
}

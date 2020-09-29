package com.xeross.anniveraire.controller.gallery.request

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.Gallery
import java.util.concurrent.Executor

class GalleryRequestViewModel(private val executor: Executor) : ViewModel() {

    companion object {
        const val GALLERY_COLLECTION = "galleries"
        const val USERS_COLLECTION = "users"
    }

    private val databaseInstanceGallery =
            FirebaseFirestore.getInstance().collection(GALLERY_COLLECTION)
    private val databaseInstanceUsers =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    fun getGallery(galleryId: String) = databaseInstanceGallery.document(galleryId).get()
    fun getUser(userId: String) = databaseInstanceUsers.document(userId).get()

    private fun updateCountGalleriesUser(id: String, galleriesId: ArrayList<String>) {
        databaseInstanceUsers.document(id).update("galleriesId", galleriesId)
    }
    private fun updateCountGalleriesId(id: String, galleriesId: ArrayList<String>) {
        databaseInstanceGallery.document(id).update("userId", galleriesId)
    }

    fun updateGalleryAndUser(gallery: Gallery, userId: String, galleriesId: ArrayList<String>?) = executor.execute {
        gallery.usersId.add(userId)
        galleriesId?.add(gallery.id)
        galleriesId?.let { updateCountGalleriesUser(userId, it) }
        updateCountGalleriesId(gallery.id, gallery.usersId)
    }

    fun galleryDeny(gallery: Gallery, userId: String, galleriesRequestId: ArrayList<String>?) {
        galleriesRequestId?.remove(gallery.id)
        galleriesRequestId?.let { updateCountGalleriesId(userId, it) }
    }
}
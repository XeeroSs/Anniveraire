package com.xeross.anniveraire.controller.gallery.request

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
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

    private fun updateCountUsersGalleriesId(id: String, usersId: ArrayList<String>) {
        databaseInstanceGallery.document(id).update("usersId", usersId)
    }

    private fun updateCountGalleriesRequest(id: String, galleriesRequestId: ArrayList<String>) {
        databaseInstanceGallery.document(id).update("galleriesRequestId", galleriesRequestId)
    }

    fun updateGalleryAndUser(gallery: Gallery, userId: String, galleriesId: ArrayList<String>?) = executor.execute {
        if (gallery.usersId.contains(userId)) return@execute
        if (galleriesId == null) return@execute
        if (galleriesId.contains(gallery.id)) return@execute
        gallery.usersId.add(userId)
        galleriesId.add(gallery.id)
        updateCountGalleriesUser(userId, galleriesId)
        updateCountUsersGalleriesId(gallery.id, gallery.usersId)
    }

    fun galleryRemove(gallery: Gallery, userId: String, galleriesRequestId: ArrayList<String>?) {
        galleriesRequestId?.remove(gallery.id)
        galleriesRequestId?.let { updateCountGalleriesRequest(gallery.id, it) }
    }
}
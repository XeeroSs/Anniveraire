package com.xeross.anniveraire.controller.gallery

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Gallery
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

class GalleryViewModel(private val executor: Executor) : ViewModel() {

    companion object {
        const val USERS_COLLECTION = "users"
        const val GALLERY_COLLECTION = "galleries"
    }

    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
    private val databaseGalleryInstance =
            FirebaseFirestore.getInstance().collection(GALLERY_COLLECTION)

    fun createGallery(gallery: Gallery, userId: String, galleriesId: ArrayList<String>?) = executor.execute {
        gallery.usersId.add(userId)
        galleriesId?.add(gallery.id)
        updateCountGalleriesUser(userId, galleriesId)
        databaseGalleryInstance.document(gallery.id).set(gallery).addOnCompleteListener {
        }
    }

    fun deleteGallery(galleryId: String) {
        databaseGalleryInstance.document(galleryId).delete()
    }

    private fun updateCountGalleriesUser(id: String, galleriesId: ArrayList<String>?) {
        databaseUsersInstance.document(id).update("galleriesId", galleriesId)
    }

    fun getUsers() = databaseUsersInstance
    fun getUser(id: String) = databaseUsersInstance.document(id).get()
    fun getGallery(id: String) = databaseGalleryInstance.document(id).get()

    fun updateGalleriesRequestUser(id: String, galleriesRequestId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("galleriesRequestId", galleriesRequestId)
    }

    fun updateGalleryName(name: String, galleryId: String) {
        databaseGalleryInstance.document(galleryId).update("name", name)
    }

    fun removeGalleryAndUser(gallery: Gallery, userId: String, galleriesId: ArrayList<String>?) = executor.execute {
        gallery.usersId.remove(userId)
        galleriesId?.remove(gallery.id)
        galleriesId?.let { updateCountGalleriesUser(userId, it) }
        updateCountGalleriesId(gallery.id, gallery.usersId)
    }

    private fun updateDateGallery(galleryId: String) {
        databaseGalleryInstance.document(galleryId).update("activityDate", Calendar.getInstance().time)
    }

    private fun updateCountGalleriesId(id: String, galleriesId: ArrayList<String>) {
        databaseGalleryInstance.document(id).update("usersId", galleriesId)
    }

    fun updateGalleries(id: String, imagesId: ArrayList<String>) {
        databaseGalleryInstance.document(id).update("imagesId", imagesId)
        updateDateGallery(id)
    }
}
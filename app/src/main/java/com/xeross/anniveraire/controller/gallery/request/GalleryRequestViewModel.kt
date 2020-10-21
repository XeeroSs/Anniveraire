package com.xeross.anniveraire.controller.gallery.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import java.util.concurrent.Executor

class GalleryRequestViewModel : ViewModel() {

    companion object {
        const val GALLERY_COLLECTION = "galleries"
        const val USERS_COLLECTION = "users"
    }

    private val databaseInstanceGallery =
            FirebaseFirestore.getInstance().collection(GALLERY_COLLECTION)
    private val databaseInstanceUsers =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    private fun getDocumentGallery(galleryId: String) = databaseInstanceGallery.document(galleryId).get()
    private fun getDocumentUser(userId: String) = databaseInstanceUsers.document(userId).get()

    fun getGalleriesFromUser(userId: String): LiveData<ArrayList<Gallery>> {
        val mutableLiveData = MutableLiveData<ArrayList<Gallery>>()
        val galleries = ArrayList<Gallery>()
        getDocumentUser(userId).addOnCompleteListener { taskUser ->
            taskUser.result?.toObject(User::class.java)?.let { user ->
                user.galleriesRequestId.forEach { gId ->
                    getDocumentGallery(gId).addOnCompleteListener { taskGallery ->
                        taskGallery.result?.toObject(Gallery::class.java)?.let { gallery ->
                            galleries.add(gallery)
                            mutableLiveData.postValue(galleries)
                        }
                    }
                }
            }
        }
        return mutableLiveData
    }

    fun getUser(userId: String): LiveData<User> {
        val mutableLiveData = MutableLiveData<User>()
        getDocumentUser(userId).addOnCompleteListener { t ->
            t.result?.toObject(User::class.java)?.let { user ->
                mutableLiveData.postValue(user)
            }
        }
        return mutableLiveData
    }

    private fun updateUsersIdFromGallery(id: String, usersId: ArrayList<String>) {
        databaseInstanceGallery.document(id).update("usersId", usersId)
    }

    private fun updateGalleryRequestsFromUser(id: String, galleriesRequestId: ArrayList<String>) {
        databaseInstanceUsers.document(id).update("galleriesRequestId", galleriesRequestId)
    }

    private fun updateGalleriesIdFromUser(id: String, galleriesId: ArrayList<String>) {
        databaseInstanceUsers.document(id).update("galleriesId", galleriesId)
    }

    fun joinGallery(gallery: Gallery, userId: String, galleriesId: ArrayList<String>?) {
        if (gallery.usersId.contains(userId)) return
        if (galleriesId == null) return
        if (galleriesId.contains(gallery.id)) return
        gallery.usersId.add(userId)
        galleriesId.add(gallery.id)
        updateGalleriesIdFromUser(userId, galleriesId)
        updateUsersIdFromGallery(gallery.id, gallery.usersId)
    }

    fun removeGalleryRequest(gallery: Gallery, userId: String, galleriesRequestId: ArrayList<String>?) {
        galleriesRequestId?.remove(gallery.id)
        galleriesRequestId?.let { updateGalleryRequestsFromUser(userId, it) }
    }
}
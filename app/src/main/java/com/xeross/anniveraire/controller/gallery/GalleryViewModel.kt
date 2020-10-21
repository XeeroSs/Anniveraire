package com.xeross.anniveraire.controller.gallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.GALLERY_COLLECTION
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION
import java.util.*
import kotlin.collections.ArrayList

class GalleryViewModel : ViewModel() {

    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
    private val databaseGalleryInstance =
            FirebaseFirestore.getInstance().collection(GALLERY_COLLECTION)

    fun createGallery(gallery: Gallery, userId: String, galleriesId: ArrayList<String>?) {
        gallery.usersId.add(userId)
        galleriesId?.add(gallery.id)
        updateGalleryIdsFromUser(userId, galleriesId)
        databaseGalleryInstance.document(gallery.id).set(gallery).addOnCompleteListener {
        }
    }

    fun deleteGallery(galleryId: String) {
        databaseGalleryInstance.document(galleryId).delete()
    }

    private fun updateGalleryIdsFromUser(id: String, galleriesId: ArrayList<String>?) {
        databaseUsersInstance.document(id).update("galleriesId", galleriesId)
    }

    fun getGalleriesFromUser(userId: String): LiveData<ArrayList<Gallery>> {
        val mutableLiveData = MutableLiveData<ArrayList<Gallery>>()
        getDocumentUser(userId).addOnCompleteListener { taskUser ->
            taskUser.result?.toObject(User::class.java)?.let { user ->
                val galleries = ArrayList<Gallery>()
                user.galleriesId.forEach { gId ->
                    Log.i("TEST", "gId: $gId")
                    getDocumentGallery(gId).addOnCompleteListener { taskGallery ->
                        val t = taskGallery
                        val result = t.result
                        val gallery = result?.toObject(Gallery::class.java)
                        Log.i("TEST", "gallery: $gallery")
                        if(gallery != null) {
                            if (!galleries.contains(gallery)) {
                                galleries.add(gallery)
                                mutableLiveData.postValue(galleries)
                            }
                        }
                     //   taskGallery.result?.toObject(Gallery::class.java)?.let { gallery ->
                     //   }
                    }.addOnFailureListener {
                        it.message
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

    fun getGallery(galleryId: String): LiveData<Gallery> {
        val mutableLiveData = MutableLiveData<Gallery>()
        getDocumentGallery(galleryId).addOnCompleteListener { t ->
            t.result?.toObject(Gallery::class.java)?.let { gallery ->
                mutableLiveData.postValue(gallery)
            }
        }
        return mutableLiveData
    }

    private fun getDocumentUser(id: String) = databaseUsersInstance.document(id).get()
    private fun getDocumentGallery(id: String) = databaseGalleryInstance.document(id).get()

    fun updateGalleryName(name: String, galleryId: String) {
        databaseGalleryInstance.document(galleryId).update("name", name)
    }

    fun removeGalleryFromUser(gallery: Gallery, userId: String, galleriesId: ArrayList<String>?) {
        gallery.usersId.remove(userId)
        galleriesId?.remove(gallery.id)
        galleriesId?.let { updateGalleryIdsFromUser(userId, it) }
        updateUserIdsFromGallery(gallery.id, gallery.usersId)
    }

    private fun updateDateGallery(galleryId: String) {
        databaseGalleryInstance.document(galleryId).update("activityDate", Calendar.getInstance().time)
    }

    private fun updateUserIdsFromGallery(id: String, galleriesId: ArrayList<String>) {
        databaseGalleryInstance.document(id).update("usersId", galleriesId)
    }

    fun updateGallery(id: String, imagesId: ArrayList<String>) {
        databaseGalleryInstance.document(id).update("imagesId", imagesId)
        updateDateGallery(id)
    }
}
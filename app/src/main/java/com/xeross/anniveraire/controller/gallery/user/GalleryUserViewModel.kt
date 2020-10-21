package com.xeross.anniveraire.controller.gallery.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.GALLERY_COLLECTION
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION

class GalleryUserViewModel : ViewModel() {

    private val databaseGalleryInstance =
            FirebaseFirestore.getInstance().collection(GALLERY_COLLECTION)
    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    private fun getDocumentUser(id: String) = databaseUsersInstance.document(id).get()
    private fun getDocumentGallery(id: String) = databaseGalleryInstance.document(id).get()
    fun getUsers() = databaseUsersInstance
    fun getUserFromGallery(galleryId: String): LiveData<ArrayList<User>> {
        val mutableLiveData = MutableLiveData<ArrayList<User>>()
        getDocumentGallery(galleryId).addOnCompleteListener { taskGallery ->
            taskGallery.result?.toObject(Gallery::class.java)?.let { gallery ->
                val users = ArrayList<User>()
                gallery.usersId.forEach { uId ->
                    getDocumentUser(uId).addOnCompleteListener { taskUser ->
                        taskUser.result?.toObject(User::class.java)?.let { user ->
                            if (!users.contains(user)) {
                                users.add(user)
                            mutableLiveData.postValue(users)
                        }}
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

    fun updateGalleryIdsFromUser(id: String, galleriesId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("galleriesId", galleriesId)

    }

    fun updateUserIdsFromGallery(id: String, usersId: ArrayList<String>) {
        databaseGalleryInstance.document(id).update("usersId", usersId)
    }

    fun updateGalleriesRequestUser(id: String, galleriesRequestId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("galleriesRequestId", galleriesRequestId)
    }
}

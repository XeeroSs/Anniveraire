package com.xeross.anniveraire.controller.gallery.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.listener.RequestContract
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.GALLERY_COLLECTION
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION

class GalleryRequestPresenter(private val contract: RequestContract.View<Gallery>) :
        ViewModel(),
        RequestContract.Presenter<Gallery> {

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

    override fun removeObjectRequest(tObject: Gallery, userId: String) {
        contract.setList()
        getDocumentUser(userId).addOnCompleteListener { t ->
            t.result?.toObject(User::class.java)?.let { user ->
                removeRequest(user.galleriesRequestId, tObject, userId)
            }
        }
    }

    private fun removeRequest(rGallery: ArrayList<String>, tObject: Gallery, userId: String) {
        rGallery.remove(tObject.id)
        updateGalleryRequestsFromUser(userId, rGallery)
        contract.getRequests()
    }

    override fun getObjectsFromUser(userId: String) {
        contract.setList()
        val galleries = ArrayList<Gallery>()
        getDocumentUser(userId).addOnCompleteListener { taskUser ->
            taskUser.result?.toObject(User::class.java)?.let { user ->
                user.galleriesRequestId.forEach { gId ->
                    getDocumentGallery(gId).addOnCompleteListener { taskGallery ->
                        taskGallery.result?.toObject(Gallery::class.java)?.let { gallery ->
                            if (!galleries.contains(gallery)) {
                                galleries.add(gallery)
                                contract.getObjectsFromUser(galleries)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun joinObject(tObject: Gallery, userId: String) {
        contract.setList()
        getDocumentUser(userId).addOnCompleteListener { t ->
            t.result?.toObject(User::class.java)?.let { user ->
                user.galleriesId.let { galleryIds ->
                    val userIds = tObject.usersId
                    if (userIds.contains(userId)) return@addOnCompleteListener
                    val galleryId = tObject.id
                    if (galleryIds.contains(galleryId)) return@addOnCompleteListener
                    userIds.add(userId)
                    galleryIds.add(galleryId)
                    updateGalleriesIdFromUser(userId, galleryIds)
                    updateUsersIdFromGallery(galleryId, userIds)
                    removeRequest(user.galleriesRequestId, tObject, userId)
                }
            }
        }
    }
}
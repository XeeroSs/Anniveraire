package com.xeross.anniveraire.controller.gallery.galleries

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class GalleriesPresenter(private val context: Context, private val contract: GalleriesContract.View) : GalleriesContract.Presenter {

    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
    private val databaseGalleryInstance =
            FirebaseFirestore.getInstance().collection(Constants.GALLERY_COLLECTION)

    private fun updateGalleryIdsFromUser(id: String, galleriesId: ArrayList<String>?) {
        databaseUsersInstance.document(id).update("galleriesId", galleriesId)
    }

    private fun getDocumentUser(id: String) = databaseUsersInstance.document(id).get()
    private fun getDocumentGallery(id: String) = databaseGalleryInstance.document(id).get()

    private fun updateUserIdsFromGallery(id: String, galleriesId: ArrayList<String>) {
        databaseGalleryInstance.document(id).update("usersId", galleriesId)
    }

    override fun getGalleries(userId: String) {
        contract.removeGalleries()
        val galleries = ArrayList<Gallery>()
        getDocumentUser(userId).addOnCompleteListener { taskUser ->
            taskUser.result?.toObject(User::class.java)?.let { user ->
                user.galleriesId.forEach { gId ->
                    getDocumentGallery(gId).addOnCompleteListener { taskGallery ->
                        taskGallery.result?.toObject(Gallery::class.java)?.let { gallery ->
                            if (!galleries.contains(gallery)) {
                                galleries.add(gallery)
                                contract.getGalleries(galleries)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun addGallery(gallery: Gallery, userId: String) {
        contract.removeGalleries()
        getDocumentUser(userId).addOnCompleteListener { t ->
            t.result?.toObject(User::class.java)?.let { user ->
                createGallery(gallery, userId, user.galleriesId)
                Toast.makeText(context, "Gallery create !", Toast.LENGTH_SHORT).show()
                contract.getGalleries()
            }
        }
    }

    private fun createGallery(gallery: Gallery, userId: String, galleriesId: ArrayList<String>?) {
        gallery.usersId.add(userId)
        galleriesId?.add(gallery.id)
        updateGalleryIdsFromUser(userId, galleriesId)
        databaseGalleryInstance.document(gallery.id).set(gallery).addOnCompleteListener {
        }
    }

    override fun deleteGallery(id: String, userId: String) {
        databaseGalleryInstance.document(id).delete()
        contract.getGalleries()
    }

    override fun leaveGallery(id: String, userId: String) {
        contract.removeGalleries()
        getDocumentGallery(id).addOnCompleteListener { gT ->
            gT.result?.toObject(Gallery::class.java)?.let { gallery ->
                getDocumentUser(userId).addOnCompleteListener { uT ->
                    uT.result?.toObject(User::class.java)?.let { user ->
                        val galleryIds = user.galleriesId
                        removeGalleryFromUser(gallery, userId, galleryIds)
                        contract.getGalleries()
                    }
                }
            }
        }
    }

    private fun removeGalleryFromUser(gallery: Gallery, userId: String, galleriesId: ArrayList<String>?) {
        gallery.usersId.remove(userId)
        galleriesId?.remove(gallery.id)
        galleriesId?.let { updateGalleryIdsFromUser(userId, it) }
        updateUserIdsFromGallery(gallery.id, gallery.usersId)
    }

    override fun updateGalleryName(id: String, newName: String) {
        contract.removeGalleries()
        databaseGalleryInstance.document(id).update("name", newName)
        Toast.makeText(context, "Name update !", Toast.LENGTH_SHORT).show()
        contract.getGalleries()
    }
}
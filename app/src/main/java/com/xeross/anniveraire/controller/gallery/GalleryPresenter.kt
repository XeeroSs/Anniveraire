package com.xeross.anniveraire.controller.gallery

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.utils.Constants
import java.util.*

class GalleryPresenter(private val contract: GalleryContract.View) : GalleryContract.Presenter {

    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
    private val databaseGalleryInstance =
            FirebaseFirestore.getInstance().collection(Constants.GALLERY_COLLECTION)

    private fun getDocumentUser(id: String) = databaseUsersInstance.document(id).get()
    private fun getDocumentGallery(id: String) = databaseGalleryInstance.document(id).get()

    override fun getImageUrls(id: String) {
        contract.removeImages()
        getDocumentGallery(id).addOnCompleteListener { t ->
            t.result?.toObject(Gallery::class.java)?.let { g ->
                contract.getImageUrls(g.imagesId)
            }
        }
    }

    override fun addImage(id: String, uri: Uri) {
        contract.removeImages()
        val uuid = UUID.randomUUID().toString()
        val storage = FirebaseStorage.getInstance().getReference(uuid)
        storage.putFile(uri).addOnSuccessListener {
            storage.downloadUrl.addOnSuccessListener { pathImageSavedInFirebase ->
                getDocumentGallery(id).addOnCompleteListener { t ->
                    t.result?.toObject(Gallery::class.java)?.let { gallery ->
                        val imagesId = gallery.imagesId
                        imagesId.add(pathImageSavedInFirebase.toString())
                        updateGallery(id, imagesId)
                        contract.getImagesUrls()
                    }
                }
            }
        }
    }

    override fun deleteImage(id: String, urlImage: String) {
        contract.removeImages()
        getDocumentGallery(id).addOnCompleteListener { t ->
            t.result?.toObject(Gallery::class.java)?.let { gallery ->
                val imagesId = gallery.imagesId
                imagesId.remove(urlImage)
                updateGallery(id, imagesId)
                contract.sendToast("Image delete !")
                contract.getImagesUrls()
            }
        }
    }

    private fun updateGallery(id: String, imagesId: ArrayList<String>) {
        databaseGalleryInstance.document(id).update("imagesId", imagesId)
        updateDateGallery(id)
    }

    private fun updateDateGallery(galleryId: String) {
        databaseGalleryInstance.document(galleryId).update("activityDate", Calendar.getInstance().time)
    }
}
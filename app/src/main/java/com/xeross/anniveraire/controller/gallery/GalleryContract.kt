package com.xeross.anniveraire.controller.gallery

import android.net.Uri
import com.xeross.anniveraire.model.Gallery

interface GalleryContract {
    interface View {
        fun removeImages()
        fun getImageUrls(tObjects: ArrayList<String>)
     fun   getImagesUrls()
        fun sendToast(testToast: String)
    }

    interface Presenter {
        fun getImageUrls(id: String)
        fun deleteImage(id: String, urlImage: String)
        fun addImage(id: String, uri: Uri)
    }
}
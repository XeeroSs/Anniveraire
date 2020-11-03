package com.xeross.anniveraire.controller.gallery.galleries

import com.xeross.anniveraire.model.Gallery

interface GalleriesContract {
    interface View {
        fun removeGalleries()
        fun getGalleries(tObjects: ArrayList<Gallery>)
        fun getGalleries()
    }

    interface Presenter {
        fun getGalleries(userId: String)
        fun addGallery(gallery: Gallery, userId: String)
        fun deleteGallery(id: String, userId: String)
        fun leaveGallery(id: String, userId: String)
        fun updateGalleryName(id: String, newName: String)
    }
}
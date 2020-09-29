package com.xeross.anniveraire.controller.gallery.request

import android.os.Bundle
import android.widget.Toast
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.GalleryRequestAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import kotlinx.android.synthetic.main.activity_gallery_request.*

class GalleryRequestActivity : BaseActivity() {
    private var viewModel: GalleryRequestViewModel? = null
    private var adapterEvent: GalleryRequestAdapter? = null
    private val galleries = ArrayList<Gallery>()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = configureViewModel()
        GalleryRequestAdapter(galleries, this).let {
            adapterEvent = it
            gallery_request_activity_recyclerview.setRecyclerViewAdapter(it)
        }
        userId = getCurrentUser()?.uid ?: return
        userId?.let { getGalleriesFromUser(it) }
    }

    private fun getGalleriesFromUser(userId: String) {
        viewModel?.let { vm ->
            vm.getUser(userId).addOnCompleteListener { taskUser ->
                taskUser.result?.toObject(User::class.java)?.let { user ->
                    user.galleriesRequestId?.forEach { gId ->
                        vm.getGallery(gId).addOnCompleteListener { taskGallery ->
                            taskGallery.result?.toObject(Gallery::class.java)?.let { gallery ->
                                galleries.add(gallery)
                                adapterEvent?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    fun joinRequest(gallery: Gallery) {
        viewModel?.let { vm ->
            userId?.let {
                vm.getUser(it).addOnCompleteListener { t ->
                    t.result?.toObject(User::class.java)?.let { user ->
                        vm.galleryDeny(gallery, it, user.galleriesRequestId)
                        vm.updateGalleryAndUser(gallery, it, user.galleriesId)
                        Toast.makeText(this, "Gallery join !", Toast.LENGTH_SHORT).show()
                        galleries.clear()
                        getGalleriesFromUser(it)
                    }
                }
            }
        }
    }

    fun deleteRequest(gallery: Gallery) {
        viewModel?.let { vm ->
            userId?.let {
                vm.getUser(it).addOnCompleteListener { t ->
                    t.result?.toObject(User::class.java)?.let { user ->
                        vm.galleryDeny(gallery, it, user.galleriesRequestId)
                        Toast.makeText(this, "Gallery request delete !", Toast.LENGTH_SHORT).show()
                        galleries.clear()
                        getGalleriesFromUser(it)
                    }
                }
            }
        }
    }

    override fun getToolBar() = R.id.gallery_request_activity_toolbar
    override fun getLayoutId() = R.layout.activity_gallery_request
}
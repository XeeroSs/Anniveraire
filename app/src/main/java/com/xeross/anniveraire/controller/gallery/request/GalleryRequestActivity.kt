package com.xeross.anniveraire.controller.gallery.request

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.RequestAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.listener.RequestListener
import com.xeross.anniveraire.model.Gallery
import kotlinx.android.synthetic.main.activity_gallery_request.*

// Activity grouping the user's gallery invitations
class GalleryRequestActivity : BaseActivity(), RequestListener<Gallery> {

    private lateinit var viewModel: GalleryRequestViewModel
    private var adapter: RequestAdapter<Gallery>? = null
    private val galleries = ArrayList<Gallery>()
    private lateinit var userId: String

    // Create activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = getCurrentUser()?.uid ?: return finish()
        viewModel = configureViewModel() ?: return finish()
        initializeRecyclerView()
        getGalleriesFromUser()
    }

    // Initialize recyclerView
    private fun initializeRecyclerView() {
        RequestAdapter(this, galleries, this).let {
            adapter = it
            gallery_request_activity_recyclerview.setRecyclerViewAdapter(it)
        }
    }

    // get all galleries from user
    private fun getGalleriesFromUser() {
        viewModel.getGalleriesFromUser(userId).observe(this, Observer {
            it?.let {
                galleries.addAll(it)
                adapter?.notifyDataSetChanged()
            }
        })
    }

    // UI
    override fun getLayoutId() = R.layout.activity_gallery_request
    override fun getToolBarTitle() = "Gallery requests"

    // When a user accepts a request
    override fun join(dObject: Gallery) {
        updateRecyclerView()
        viewModel.getUser(userId).observe(this, Observer {
            it?.let { user ->
                viewModel.joinGallery(dObject, userId, user.galleriesId)
                viewModel.removeGalleryRequest(dObject, userId, user.galleriesRequestId)
                Toast.makeText(this, "Gallery join !", Toast.LENGTH_SHORT).show()
                getGalleriesFromUser()
            }
        })
    }

    // When a user refuses an invitation
    override fun deny(dObject: Gallery) {
        updateRecyclerView()
        viewModel.getUser(userId).observe(this, Observer {
            it?.let { user ->
                viewModel.removeGalleryRequest(dObject, userId, user.galleriesRequestId)
                Toast.makeText(this, "Gallery request delete !", Toast.LENGTH_SHORT).show()
                getGalleriesFromUser()
            }
        })
    }

    // Update recyclerView
    private fun updateRecyclerView() {
        galleries.clear()
        adapter?.notifyDataSetChanged()
    }
}
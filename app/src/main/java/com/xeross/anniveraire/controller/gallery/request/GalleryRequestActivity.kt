package com.xeross.anniveraire.controller.gallery.request

import android.os.Bundle
import android.widget.Toast
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.RequestAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.listener.RequestContract
import com.xeross.anniveraire.listener.RequestListener
import com.xeross.anniveraire.model.Gallery
import kotlinx.android.synthetic.main.activity_gallery_request.*

// Activity grouping the user's gallery invitations
class GalleryRequestActivity : BaseActivity(), RequestListener<Gallery>, RequestContract.View<Gallery> {

    private lateinit var presenter: GalleryRequestPresenter
    private var adapter: RequestAdapter<Gallery>? = null
    private val galleries = ArrayList<Gallery>()
    private lateinit var userId: String

    // Create activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = getCurrentUser()?.uid ?: return finish()
        presenter = GalleryRequestPresenter(this)
        initializeRecyclerView()
        presenter.getObjectsFromUser(userId)
    }

    // Initialize recyclerView
    private fun initializeRecyclerView() {
        RequestAdapter(this, galleries, this).let {
            adapter = it
            gallery_request_activity_recyclerview.setRecyclerViewAdapter(it)
        }
    }

    override fun getRequests() {
        presenter.getGalleriesFromUser(userId)
    }

    // UI
    override fun getLayoutId() = R.layout.activity_gallery_request
    override fun getToolBarTitle() = "Gallery requests"

    // Update recyclerView
    private fun updateRecyclerView() {
        galleries.clear()
        adapter?.notifyDataSetChanged()
    }

    override fun getObjectsFromUser(tObjects: ArrayList<Gallery>) {
        this.galleries.clear()
        this.galleries.addAll(tObjects)
        adapter?.notifyDataSetChanged()
    }

    override fun setList() {
        galleries.clear()
        adapter?.notifyDataSetChanged()
    }

    // When a user accepts invitation
    override fun join(dObject: Gallery) {
        presenter.joinObject(dObject, userId)
        Toast.makeText(this, "Gallery join !", Toast.LENGTH_SHORT).show()
    }

    // When a user refuses invitation
    override fun deny(dObject: Gallery) {
        presenter.removeObjectRequest(dObject, userId)
        Toast.makeText(this, "Gallery request delete !", Toast.LENGTH_SHORT).show()
    }
}
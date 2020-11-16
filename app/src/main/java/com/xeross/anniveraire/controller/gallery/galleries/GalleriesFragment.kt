package com.xeross.anniveraire.controller.gallery.galleries

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.GalleriesAdapter
import com.xeross.anniveraire.controller.base.BaseFragment
import com.xeross.anniveraire.controller.gallery.GalleryActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.bsd_confirm.view.*
import kotlinx.android.synthetic.main.bsd_discussion.view.*
import kotlinx.android.synthetic.main.bsd_item_leave.view.*
import kotlinx.android.synthetic.main.bsd_item_selected.view.*
import kotlinx.android.synthetic.main.fragment_galleries.*
import java.util.*
import kotlin.collections.ArrayList

class GalleriesFragment : BaseFragment(), ClickListener<Gallery>, GalleriesContract.View {

    private lateinit var presenter: GalleriesPresenter
    private var adapter: GalleriesAdapter? = null
    private val galleries = ArrayList<Gallery>()
    private val galleriesFull = ArrayList<Gallery>()
    private lateinit var userId: String

    // Bottom sheet dialog for create a new gallery
    @SuppressLint("InflateParams")
    private fun createBSDGallery(galleryId: String?) {
        LayoutInflater.from(context).inflate(R.layout.bsd_discussion, null).let { view ->
            val alertDialog = createBSD(view)

            view.bsd_discussion_button_add.setOnClickListener {
                if (view.bsd_discussion_edittext.text!!.isEmpty()) {
                    sendMissingInformationMessage()
                    return@setOnClickListener
                }

                alertDialog?.dismiss()
                if (galleryId == null) {
                    val gallery = Gallery(name = view.bsd_discussion_edittext.text.toString(), ownerId = userId, activityDate = Date())
                    presenter.addGallery(gallery, userId)
                    return@setOnClickListener
                }
                presenter.updateGalleryName(galleryId, view.bsd_discussion_edittext.text.toString())
            }
        }
    }

    override fun getFragmentId() = R.layout.fragment_galleries
    override fun setFragment() = this

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter = GalleriesPresenter(this)
        initializeRecyclerView()
        userId = getCurrentUser()?.uid ?: return
    }

    // search gallery
    override fun onSearch(searchView: SearchView) {
        searchEvent(searchView)
    }

    override fun sendToast(textToast: String) {
        Toast.makeText(context, textToast, Toast.LENGTH_SHORT).show()
    }

    // search gallery
    private fun searchEvent(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return true
            }

        })
    }

    // add Gallery
    override fun onAdd() {
        createBSDGallery(null)
    }

    // initialize recyclerView
    private fun initializeRecyclerView() {
        context?.let { c ->
            GalleriesAdapter(galleries, galleriesFull, this, c).let {
                adapter = it
                galleries_activity_list.setRecyclerViewAdapter(it)
            }
        }
    }

    // Get all galleries from user in firebase
    private fun getGalleriesFromUser() {
        presenter.getGalleries(userId)
    }

    override fun onStart() {
        super.onStart()
        getGalleriesFromUser()
    }

    // click on gallery item
    override fun onClick(o: Gallery) {
        val intent = Intent(context, GalleryActivity::class.java)
        intent.putExtra(Constants.ID_GALLERY, o.id)
        startActivity(intent)
    }

    // Long click on gallery item
    override fun onLongClick(o: Gallery) {
        // Check if the user's gallery owner
        if (o.ownerId == userId) {
            itemSelectedOwnerGallery(o)
            return
        }
        itemSelected(o)
    }

    // Bottom sheet dialog -> item selected by user
    @SuppressLint("InflateParams")
    private fun itemSelected(gallery: Gallery) {
        LayoutInflater.from(context).inflate(R.layout.bsd_item_leave, null).run {

            val bottomSheetDialog = createBSD(this)

            bsd_item_selected_leave.setOnClickListener {
                confirmLeave(gallery)
                bottomSheetDialog?.dismiss()
            }
        }

    }

    // Bottom sheet dialog -> item selected by gallery owner
    @SuppressLint("InflateParams")
    private fun itemSelectedOwnerGallery(gallery: Gallery) {
        LayoutInflater.from(context).inflate(R.layout.bsd_item_selected, null).run {

            val bottomSheetDialog = createBSD(this)

            bsd_item_selected_edit.setOnClickListener {
                createBSDGallery(gallery.id)
                bottomSheetDialog?.dismiss()
            }
            bsd_item_selected_delete.setOnClickListener {
                confirmDelete(gallery)
                bottomSheetDialog?.dismiss()
            }
        }

    }

    // Bottom sheet dialog -> confirm delete gallery
    @SuppressLint("InflateParams")
    private fun confirmDelete(gallery: Gallery) {
        LayoutInflater.from(context).inflate(R.layout.bsd_confirm_deleted_permanently, null).let {

            val bottomSheetDialog = createBSD(it)

            it.bsd_confirm_yes.setOnClickListener {
                // delete
                bottomSheetDialog?.dismiss()
                presenter.deleteGallery(gallery.id, userId)
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog?.dismiss()
            }
        }

    }

    // Bottom sheet dialog -> confirm leave gallery
    @SuppressLint("InflateParams")
    private fun confirmLeave(gallery: Gallery) {
        LayoutInflater.from(context).inflate(R.layout.bsd_confirm_leave, null).let { view ->

            val bottomSheetDialog = createBSD(view)

            view.bsd_confirm_yes.setOnClickListener { _ ->

                bottomSheetDialog?.dismiss()
                presenter.leaveGallery(gallery.id, userId)
            }
            view.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog?.dismiss()
            }
        }

    }

    override fun getGalleries() {
        presenter.getGalleries(userId)
    }

    // Update recyclerView
    override fun removeGalleries() {
        galleries.clear()
        galleriesFull.clear()
        adapter?.notifyDataSetChanged()
    }

    override fun getGalleries(tObjects: ArrayList<Gallery>) {
        galleries.clear()
        galleriesFull.clear()
        galleries.addAll(tObjects)
        galleriesFull.addAll(tObjects)
        adapter?.notifyDataSetChanged()
    }
}

package com.xeross.anniveraire.controller.gallery

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.GalleryAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.controller.gallery.details.GalleryDetailActivity
import com.xeross.anniveraire.controller.gallery.user.GalleryUserActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.utils.Constants
import com.xeross.anniveraire.utils.Constants.RC_CHOOSE_PHOTO
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.bsd_item_leave.view.*
import permissions.dispatcher.*

@RuntimePermissions
class GalleryActivity : BaseActivity(), ClickListener<String>, GalleryContract.View {

    private val urls = ArrayList<String>()
    private var adapter: GalleryAdapter? = null

    // Non null
    private lateinit var userId: String

    // Non null
    private lateinit var galleryId: String

    // Non null
    private lateinit var presenter: GalleryPresenter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_group, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(Constants.ID_GALLERY)?.let { s ->
            galleryId = s
        } ?: finish()
        presenter = GalleryPresenter(this, this)
        userId = getCurrentUser()?.uid ?: return finish()
        onClick()
        initializeRecyclerView()
        getUrls()
    }

    // Initialize recyclerView
    private fun initializeRecyclerView() {
        GalleryAdapter(urls, this, this).let {
            adapter = it
            gallery_activity_recyclerview.layoutManager = GridLayoutManager(this, 3)
            gallery_activity_recyclerview.setRecyclerViewAdapter(it, true)
        }
    }

    // Click UI
    private fun onClick() {
        activity_gallery_fab.setOnClickListener {
            showGalleryWithPermissionCheck()
        }
    }

    // Get image's urls of gallery
    private fun getUrls() {
        presenter.getImageUrls(galleryId)
    }

    override fun getLayoutId() = R.layout.activity_gallery
    override fun getToolBarTitle() = "Gallery"

    // Click Toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.toolbar_options -> startActivityDetailPicture()
        }
        return super.onOptionsItemSelected(item)
    }

    // Start activity
    private fun startActivityDetailPicture() {
        val intent = Intent(this, GalleryUserActivity::class.java)
        intent.putExtra(Constants.ID_GALLERY, galleryId)
        startActivity(intent)
    }

    // upload image & get url
    private fun uploadPhotoInFirebase(uri: Uri) {
        presenter.addImage(galleryId, uri)
    }

    // Response
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleResponse(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    // Launch the selection image Activity
    @NeedsPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showGallery() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RC_CHOOSE_PHOTO)
    }

    // Handle activity response (after user has chosen or not a picture)
    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                data?.data?.let { dataData -> uploadPhotoInFirebase(dataData) }
                return
            } else Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show()
        }
    }

    override fun getImagesUrls() {
        presenter.getImageUrls(galleryId)
    }

    // Bottom sheet dialog -> Item selected
    @SuppressLint("InflateParams")
    private fun itemSelected(url: String) {
        LayoutInflater.from(this).inflate(R.layout.bsd_delete, null).let { view ->

            val bottomSheetDialog = createBSD(view)

            view.bsd_item_selected_leave.setOnClickListener { _ ->
                bottomSheetDialog.dismiss()
                presenter.deleteImage(galleryId, url)
            }
        }

    }

    // Popup for permission
    @OnShowRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showRationaleForPermission(request: PermissionRequest) {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.need_permission))
                .setMessage(getString(R.string.missing_permission))
                .setPositiveButton(android.R.string.yes) { _, _ -> request.proceed() }
                .setNegativeButton(android.R.string.no) { _, _ -> request.cancel() }
                .show()
    }

    @OnPermissionDenied(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onPermissionDenied() {
        Toast.makeText(this, getString(R.string.missing_permission), Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onPermissionNeverAskAgain() {
        Toast.makeText(this, getString(R.string.missing_permission), Toast.LENGTH_SHORT).show()
    }

    // Click image item
    override fun onClick(o: String) {
        Intent(this, GalleryDetailActivity::class.java).let {
            it.putExtra(Constants.URL_IMAGE, o)
            startActivity(it)
        }
    }

    // Long click image item
    override fun onLongClick(o: String) {
        itemSelected(o)
    }

    override fun removeImages() {
        urls.clear()
        adapter?.notifyDataSetChanged()
    }

    override fun getImageUrls(tObjects: ArrayList<String>) {
        urls.addAll(tObjects)
        adapter?.notifyDataSetChanged()
    }
}
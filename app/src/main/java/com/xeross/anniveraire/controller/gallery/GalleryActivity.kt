package com.xeross.anniveraire.controller.gallery

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
import com.google.firebase.storage.FirebaseStorage
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.GalleryAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.controller.gallery.user.GalleryUserActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.bsd_item_leave.view.*
import permissions.dispatcher.*
import java.util.*
import kotlin.collections.ArrayList

@RuntimePermissions
class GalleryActivity : BaseActivity(), ClickListener<String> {

    companion object {
        const val RC_CHOOSE_PHOTO = 2
    }

    private val urls = ArrayList<String>()
    private var adapter: GalleryAdapter? = null
    private var user: User? = null
    private lateinit var galleryId: String
    private var viewModel: GalleryViewModel? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_group, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(Constants.ID_GALLERY)?.let { s ->
            galleryId = s
        } ?: finish()

        viewModel = configureViewModel()
        getCurrentUserFromFirestore()
        activity_gallery_fab.setOnClickListener {
            showGalleryWithPermissionCheck()
        }
        GalleryAdapter(urls, this, this).let {
            adapter = it
            gallery_activity_recyclerview.layoutManager = GridLayoutManager(this, 3)
            gallery_activity_recyclerview.setRecyclerViewAdapter(it, true)
        }
        getUrls()
    }

    private fun getUrls() {
        urls.clear()
        viewModel?.let { vm ->
            vm.getGallery(galleryId).addOnSuccessListener { d ->
                d.toObject(Gallery::class.java)?.let { g ->
                    g.imagesId.forEach {
                        urls.add(it)
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getToolBar() = R.id.gallery_activity_toolbar

    override fun getLayoutId() = R.layout.activity_gallery

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.toolbar_options -> {
                val intent = Intent(this, GalleryUserActivity::class.java)
                intent.putExtra(Constants.ID_GALLERY, galleryId)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadPhotoInFirebase(uri: Uri) {
        val uuid = UUID.randomUUID().toString() // GENERATE UNIQUE STRING
        val imageRef = FirebaseStorage.getInstance().getReference(uuid)
        imageRef.putFile(uri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { pathImageSavedInFirebase ->
                // SAVE MESSAGE IN FIRESTORE
                viewModel?.let { vm ->
                    vm.getGallery(galleryId).addOnSuccessListener { dd ->
                        dd.toObject(Gallery::class.java)?.let { g ->
                            val imagesId = g.imagesId
                            imagesId.add(pathImageSavedInFirebase.toString())
                            vm.updateGallery(galleryId, imagesId)
                            getUrls()
                        }
                    }
                }
            }
        }
    }

    private fun getCurrentUserFromFirestore() {
        getCurrentUser()?.let { u ->
            viewModel?.getUser(u.uid)?.addOnSuccessListener {
                user = it.toObject(User::class.java)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleResponse(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showGallery() {
        // 3 - Launch an "Selection Image" Activity
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RC_CHOOSE_PHOTO)
    }

    // 4 - Handle activity response (after user has chosen or not a picture)
    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                data?.let {
                    it.data?.let { it1 -> uploadPhotoInFirebase(it1) }
                }
                return
            } else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun itemSelected(url: String) {
        LayoutInflater.from(this).inflate(R.layout.bsd_delete, null).let {

            val bottomSheetDialog = createBSD(it)

            it.bsd_item_selected_leave.setOnClickListener {
                viewModel?.let { vm ->
                    vm.getGallery(galleryId).addOnSuccessListener { dd ->
                        dd.toObject(Gallery::class.java)?.let { g ->
                            val imagesId = g.imagesId
                            imagesId.remove(url)
                            vm.updateGallery(galleryId, imagesId)
                            Toast.makeText(this, "Image delete !", Toast.LENGTH_SHORT).show()
                            getUrls()
                        }
                    }
                }
                bottomSheetDialog.dismiss()
            }
        }

    }

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

    override fun onClick(o: String) {
        Intent(this, GalleryDetailActivity::class.java).let {
            it.putExtra(Constants.URL_IMAGE, o)
            startActivity(it)
        }
    }

    override fun onLongClick(o: String) {
        itemSelected(o)
    }
}
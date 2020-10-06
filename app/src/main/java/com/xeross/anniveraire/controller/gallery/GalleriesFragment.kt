package com.xeross.anniveraire.controller.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.GalleriesAdapter
import com.xeross.anniveraire.controller.BaseFragment
import com.xeross.anniveraire.controller.gallery.request.GalleryRequestActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.bsd_confirm.view.*
import kotlinx.android.synthetic.main.bsd_discussion.view.*
import kotlinx.android.synthetic.main.bsd_item_leave.view.*
import kotlinx.android.synthetic.main.bsd_item_selected.view.*
import kotlinx.android.synthetic.main.fragment_galleries.*
import java.util.*
import kotlin.collections.ArrayList

class GalleriesFragment : BaseFragment(), ClickListener<Gallery> {

    private var viewModel: GalleryViewModel? = null
    private var adapter: GalleriesAdapter? = null
    private val galleries = ArrayList<Gallery>()
    private val galleriesFull = ArrayList<Gallery>()

    private fun createBSDGallery(galleryId: String?) {
        LayoutInflater.from(context).inflate(R.layout.bsd_discussion, null).let { view ->
            val alertDialog = createBSD(view)

            view.bsd_discussion_button_add.setOnClickListener {
                if (view.bsd_discussion_edittext.text!!.isEmpty()) {
                    sendMissingInformationMessage()
                    return@setOnClickListener
                }

                val userId = getCurrentUser()?.uid ?: return@setOnClickListener

                viewModel?.let { vm ->
                    if (galleryId == null) {
                        val gallery = Gallery(name = view.bsd_discussion_edittext.text.toString(), ownerId = userId, activityDate = Date())
                        vm.getUser(userId).addOnCompleteListener { t ->
                            t.result?.toObject(User::class.java)?.let { user ->
                                viewModel?.createGallery(gallery, userId, user.galleriesId)
                                Toast.makeText(context, "Gallery create !", Toast.LENGTH_SHORT).show()
                                galleries.clear()
                                galleriesFull.clear()
                                getGalleriesFromUser(userId)
                                alertDialog?.dismiss()
                            }
                        }
                    } else {
                        vm.updateGalleryName(view.bsd_discussion_edittext.text.toString(), galleryId)
                        Toast.makeText(context, "Name update !", Toast.LENGTH_SHORT).show()
                        galleries.clear()
                        galleriesFull.clear()
                        getGalleriesFromUser(userId)
                        alertDialog?.dismiss()
                    }
                }
            }
        }
    }

    override fun getFragmentId() = R.layout.fragment_galleries

    override fun setFragment() = this

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = configureViewModel()
        initializeRecyclerView()
        val userId = getCurrentUser()?.uid ?: return
        getGalleriesFromUser(userId)
    }

    override fun onRequest() {
        startActivity(Intent(activity, GalleryRequestActivity::class.java))
    }

    override fun onSearch(searchView: SearchView) {
        searchEvent(searchView)
    }

    private fun searchEvent(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return true
            }

        })
    }

    override fun onAdd() {
        createBSDGallery(null)
    }

    private fun initializeRecyclerView() {
        context?.let { c ->
            GalleriesAdapter(galleries, galleriesFull, this, c).let {
                adapter = it
                galleries_activity_list.setRecyclerViewAdapter(it)
            }
        }
    }

    private fun getGalleriesFromUser(userId: String) {
        viewModel?.let { vm ->
            vm.getUser(userId).addOnCompleteListener { taskUser ->
                taskUser.result?.toObject(User::class.java)?.let { user ->
                    user.galleriesId.forEach { gId ->
                        vm.getGallery(gId).addOnCompleteListener { taskGallery ->
                            taskGallery.result?.toObject(Gallery::class.java)?.let { gallery ->
                                galleries.add(gallery)
                                galleriesFull.add(gallery)
                                galleries.sortList()
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onClick(o: Gallery) {
        val intent = Intent(context, GalleryActivity::class.java)
        intent.putExtra(Constants.ID_GALLERY, o.id)
        startActivity(intent)
    }

    override fun onLongClick(o: Gallery) {
        viewModel?.getGallery(o.id)?.addOnSuccessListener { document ->
            document.toObject(Gallery::class.java)?.let { d ->
                if (o.ownerId == getCurrentUser()?.uid) {
                    itemSelectedOwnerGallery(d)
                    return@addOnSuccessListener
                }
                itemSelected(d)
            }
        }
    }

    private fun itemSelected(gallery: Gallery) {
        LayoutInflater.from(context).inflate(R.layout.bsd_item_leave, null).run {

            val bottomSheetDialog = createBSD(this)

            bsd_item_selected_leave.setOnClickListener {
                confirmLeave(gallery)
                bottomSheetDialog?.dismiss()
            }
        }

    }

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

    private fun confirmDelete(gallery: Gallery) {
        LayoutInflater.from(context).inflate(R.layout.bsd_confirm_deleted_permanently, null).let {

            val bottomSheetDialog = createBSD(it)

            val userUid = getCurrentUser()?.uid ?: return

            it.bsd_confirm_yes.setOnClickListener {
                // delete
                bottomSheetDialog?.dismiss()
                viewModel?.deleteGallery(gallery.id)
                galleries.clear()
                galleriesFull.clear()
                getGalleriesFromUser(userUid)
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog?.dismiss()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        galleries.clear()
        galleriesFull.clear()
        adapter?.notifyDataSetChanged()
        val userId = getCurrentUser()?.uid ?: return
        getGalleriesFromUser(userId)
    }

    private fun confirmLeave(gallery: Gallery) {
        LayoutInflater.from(context).inflate(R.layout.bsd_confirm_leave, null).let {

            val bottomSheetDialog = createBSD(it)

            it.bsd_confirm_yes.setOnClickListener {

                val userId = getCurrentUser()?.uid ?: return@setOnClickListener

                // delete
                bottomSheetDialog?.dismiss()
                viewModel?.let { vm ->
                    vm.getUser(userId).addOnSuccessListener { d ->
                        d.toObject(User::class.java)?.let { u ->
                            val galleryIds = u.galleriesId ?: ArrayList()
                            vm.removeGalleryAndUser(gallery, userId, galleryIds)
                            galleries.clear()
                            galleriesFull.clear()
                            getGalleriesFromUser(userId)
                        }
                    }
                }
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog?.dismiss()
            }
        }

    }

    private fun ArrayList<Gallery>.sortList() {
        sortWith(Comparator { g1, g2 ->
            g1.activityDate.compareTo(g2.activityDate);
        })
        reverse()
    }
}

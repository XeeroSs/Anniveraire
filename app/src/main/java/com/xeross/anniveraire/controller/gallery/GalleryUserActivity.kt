package com.xeross.anniveraire.controller.gallery

import android.os.Bundle
import android.view.LayoutInflater
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.UserAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.activity_gallery_user.*
import kotlinx.android.synthetic.main.bsd_confirm.view.*

class GalleryUserActivity : BaseActivity(), ClickListener<User> {

    private val usersInGallery = ArrayList<User>()
    private var viewModel: GalleryUserViewModel? = null
    private var adapter: UserAdapter? = null
    private lateinit var galleryId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(Constants.ID_GALLERY)?.let { s ->
            viewModel = configureViewModel()
            galleryId = s
            UserAdapter(usersInGallery, this, this).let {
                adapter = it
                activity_gallery_user_recyclerview.setRecyclerViewAdapter(it)
            }
            getUsers()
        } ?: finish()
    }

    private fun getUsers() {
        viewModel?.let { vm ->
            vm.getGallery(galleryId).addOnSuccessListener { dsD ->
                dsD.toObject(Gallery::class.java)?.let { d ->
                    d.usersId.forEach { userId ->
                        vm.getUser(userId).addOnSuccessListener { dsU ->
                            dsU.toObject(User::class.java)?.let { u ->
                                usersInGallery.add(u)
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getToolBar() = R.id.activity_gallery_user_toolbar
    override fun getLayoutId() = R.layout.activity_gallery_user

    override fun onClick(o: User) {}

    override fun onLongClick(o: User) {
        viewModel?.getGallery(galleryId)?.addOnSuccessListener { document ->
            document.toObject(Discussion::class.java)?.let { d ->
                d.ownerId.takeIf { it != "" }?.let { userId ->
                    if (userId == getCurrentUser()?.uid) {
                        if (o.id == getCurrentUser()?.uid) return@addOnSuccessListener
                        confirm(o)
                    }
                }
            }
        }
    }

    private fun confirm(user: User) {
        LayoutInflater.from(this).inflate(R.layout.bsd_confirm_delete, null).let {

            val bottomSheetDialog = createBSD(it)

            it.bsd_confirm_yes.setOnClickListener {
                // delete
                bottomSheetDialog.dismiss()
                viewModel?.let { vm ->
                    vm.getUser(user.id).addOnSuccessListener { d ->
                        d.toObject(User::class.java)?.let { u ->
                            val galleryIds = u.galleriesId ?: ArrayList()
                            galleryIds.remove(galleryId)
                            vm.updateGalleryUser(u.id, galleryIds)
                            usersInGallery.clear()
                            getUsers()
                        }
                    }
                }
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }

    }

}
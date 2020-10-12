package com.xeross.anniveraire.controller.gallery.user

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.UserAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.bsd_confirm.view.*
import kotlinx.android.synthetic.main.bsd_discussion.view.*
import java.util.*
import kotlin.collections.ArrayList

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
                activity_user_list.setRecyclerViewAdapter(it)
            }
            getUsers()
            activity_user_fab.setOnClickListener {
                viewModel?.getGallery(galleryId)?.addOnSuccessListener { document ->
                    document.toObject(Gallery::class.java)?.let { d ->
                        d.ownerId.takeIf { it != "" }?.let { userId ->
                            if (userId == getCurrentUser()?.uid) {
                                createBSDAddUser()
                                return@addOnSuccessListener
                            }
                        }
                        Toast.makeText(this, getString(R.string.you_cannot_add_anyone), Toast.LENGTH_SHORT).show()
                    }
                }
            }
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

    private fun createBSDAddUser() {
        LayoutInflater.from(this).inflate(R.layout.bsd_discussion, null).let { view ->
            val alertDialog = createBSD(view)

            view.bsd_discussion_button_add.setOnClickListener {
                if (view.bsd_discussion_edittext.text?.isEmpty() == true) {
                    sendMissingInformationMessage()
                    return@setOnClickListener
                }

                val userEmail = getCurrentUser()?.email ?: return@setOnClickListener

                val targetEmail = view.bsd_discussion_edittext.text.toString()

                if (targetEmail.equals(userEmail, true)) {
                    Toast.makeText(this, getString(R.string.you_cannot_add_yourself), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                viewModel?.let { vm ->
                    vm.getUsers().whereEqualTo("email", targetEmail.toLowerCase(Locale.ROOT)).get().addOnSuccessListener {
                        it.documents.forEach { d ->
                            d.toObject(User::class.java)?.let { u ->
                                val galleriesRequestId = u.galleriesRequestId
                                galleriesRequestId.add(galleryId)
                                vm.updateGalleriesRequestUser(u.id, galleriesRequestId)
                                Toast.makeText(this, getString(R.string.request_sent), Toast.LENGTH_SHORT).show()
                                alertDialog.dismiss()
                                return@addOnSuccessListener
                            }
                        }
                        Toast.makeText(this, getString(R.string.error_email_not_found), Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, getString(R.string.error_email_not_found), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun getToolBar() = R.id.activity_user_toolbar
    override fun getLayoutId() = R.layout.activity_users

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
                    usersInGallery.clear()
                    adapter?.notifyDataSetChanged()
                    vm.getUser(user.id).addOnSuccessListener { d ->
                        d.toObject(User::class.java)?.let { u ->
                            val galleryIds = u.galleriesId ?: ArrayList()
                            galleryIds.remove(galleryId)
                            vm.updateGalleryUser(u.id, galleryIds)
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
package com.xeross.anniveraire.controller.gallery.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.UserAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.bsd_add_user.view.*
import kotlinx.android.synthetic.main.bsd_confirm.view.*
import kotlinx.android.synthetic.main.bsd_discussion.view.*
import kotlinx.android.synthetic.main.bsd_discussion.view.bsd_discussion_edittext
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
                viewModel?.getGallery(galleryId)?.observe(this, androidx.lifecycle.Observer { document ->
                    document?.let { d ->
                        d.ownerId.takeIf { it != "" }?.let { userId ->
                            if (userId == getCurrentUser()?.uid) {
                                createBSDAddUser()
                                return@Observer
                            }
                        }
                    }
                    Toast.makeText(this, getString(R.string.you_cannot_add_anyone), Toast.LENGTH_SHORT).show()
                })
            }
        } ?: finish()
    }

    private fun getUsers() {
        viewModel?.getUserFromGallery(galleryId)?.observe(this, androidx.lifecycle.Observer {
            it?.let { users ->
                usersInGallery.clear()
                adapter?.notifyDataSetChanged()
                usersInGallery.addAll(users)
                adapter?.notifyDataSetChanged()
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun createBSDAddUser() {
        LayoutInflater.from(this).inflate(R.layout.bsd_add_user, null).let { view ->
            val alertDialog = createBSD(view)

            view.bsd_add_user_button_add.setOnClickListener {
                if (view.bsd_add_user_edittext.text?.isEmpty() == true) {
                    sendMissingInformationMessage()
                    return@setOnClickListener
                }

                val userEmail = getCurrentUser()?.email ?: return@setOnClickListener

                val targetEmail = view.bsd_add_user_edittext.text.toString()

                if (targetEmail.equals(userEmail, true)) {
                    Toast.makeText(this, getString(R.string.you_cannot_add_yourself), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                alertDialog.dismiss()
                viewModel?.let { vm ->
                    vm.getUsers().whereEqualTo("email", targetEmail.toLowerCase(Locale.ROOT)).get().addOnSuccessListener {
                        it.documents.forEach { d ->
                            d.toObject(User::class.java)?.let { u ->
                                val galleriesRequestId = u.galleriesRequestId
                                galleriesRequestId.add(galleryId)
                                vm.updateGalleriesRequestUser(u.id, galleriesRequestId)
                                Toast.makeText(this, getString(R.string.request_sent), Toast.LENGTH_SHORT).show()
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

    override fun getLayoutId() = R.layout.activity_users
    override fun getToolBarTitle() = "Gallery members"

    override fun onClick(o: User) {}

    override fun onLongClick(o: User) {
        viewModel?.getGallery(galleryId)?.observe(this, androidx.lifecycle.Observer { g ->
            g?.let { gallery ->
                gallery.ownerId.takeIf { it != "" }?.let { userId ->
                    if (userId == getCurrentUser()?.uid) {
                        if (o.id == getCurrentUser()?.uid) return@Observer
                        confirm(o)
                    }
                }
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun confirm(user: User) {
        LayoutInflater.from(this).inflate(R.layout.bsd_confirm_delete, null).let { view ->

            val bottomSheetDialog = createBSD(view)

            view.bsd_confirm_yes.setOnClickListener { _ ->
                // delete
                bottomSheetDialog.dismiss()
                viewModel?.let { vm ->
                    usersInGallery.clear()
                    adapter?.notifyDataSetChanged()
                    vm.getUser(user.id).observe(this, androidx.lifecycle.Observer {
                        it?.let { user ->
                            vm.getGallery(galleryId).observe(this, androidx.lifecycle.Observer { g ->
                                g?.let { gallery ->
                                    val galleryIds = user.galleriesId
                                    val userIds = gallery.usersId
                                    userIds.remove(user.id)
                                    galleryIds.remove(galleryId)
                                    vm.updateGalleryIdsFromUser(user.id, galleryIds)
                                    vm.updateUserIdsFromGallery(galleryId, userIds)
                                    getUsers()
                                }
                            })
                        }
                    })
                }
            }
            view.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }

    }

}
package com.xeross.anniveraire.controller.gallery.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.UserAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.listener.UserContract
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.bsd_add_user.view.*
import kotlinx.android.synthetic.main.bsd_confirm.view.*
import java.util.*
import kotlin.collections.ArrayList

class GalleryUserActivity : BaseActivity(), ClickListener<User>, UserContract.View {

    private val usersInGallery = ArrayList<User>()
    private var presenter: GalleryUserPresenter? = null
    private var adapter: UserAdapter? = null
    private lateinit var galleryId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(Constants.ID_GALLERY)?.let { s ->
            presenter = GalleryUserPresenter(this, this)
            galleryId = s
            UserAdapter(usersInGallery, this, this).let {
                adapter = it
                activity_user_list.setRecyclerViewAdapter(it)
            }
            presenter?.getObjectsFromUser(galleryId)
            activity_user_fab.setOnClickListener {
                getCurrentUser()?.uid?.let { presenter?.isOwnerUser(galleryId, it) }
            }
        } ?: finish()
    }

    override fun getUsers() {
        presenter?.getObjectsFromUser(galleryId)
    }

    @SuppressLint("InflateParams")
    override fun showPopupAddUser() {
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

                presenter?.sendRequestByEmail(galleryId, targetEmail.toLowerCase(Locale.ROOT), alertDialog)
            }
        }
    }

    override fun getLayoutId() = R.layout.activity_users
    override fun getToolBarTitle() = "Gallery members"

    override fun onClick(o: User) {}

    override fun onLongClick(o: User) {
        getCurrentUser()?.uid?.let {
            presenter?.longClick(galleryId, it, o.id)
        }
    }

    @SuppressLint("InflateParams")
    override fun showPopupConfirmSuppress(userId: String) {
        LayoutInflater.from(this).inflate(R.layout.bsd_confirm_delete, null).let { view ->

            val bottomSheetDialog = createBSD(view)

            view.bsd_confirm_yes.setOnClickListener { _ ->
                // delete
                bottomSheetDialog.dismiss()
                presenter?.removeUser(userId, galleryId)
            }
            view.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }

    }

    override fun setList() {
        this.usersInGallery.clear()
        adapter?.notifyDataSetChanged()
    }

    override fun getUsersFromObject(tObjects: ArrayList<User>) {
        this.usersInGallery.clear()
        this.usersInGallery.addAll(tObjects)
        adapter?.notifyDataSetChanged()
    }
}
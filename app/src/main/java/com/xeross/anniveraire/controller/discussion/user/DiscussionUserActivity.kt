package com.xeross.anniveraire.controller.discussion.user

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
import kotlinx.android.synthetic.main.bsd_discussion.view.bsd_discussion_button_add
import java.util.*
import kotlin.collections.ArrayList

class DiscussionUserActivity : BaseActivity(), ClickListener<User> {

    private val usersInDiscussion = ArrayList<User>()
    private var viewModel: DiscussionUserViewModel? = null
    private var adapter: UserAdapter? = null
    private lateinit var discussionId: String

    override fun getToolBarTitle() = "Discussion members"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(Constants.ID_DISCUSSION)?.let { s ->
            viewModel = configureViewModel()
            discussionId = s
            UserAdapter(usersInDiscussion, this, this).let {
                adapter = it
                activity_user_list.setRecyclerViewAdapter(it)
            }
            getUsers()
            activity_user_fab.setOnClickListener {
                viewModel?.getDiscussion(discussionId)?.observe(this, androidx.lifecycle.Observer { document ->
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

                viewModel?.let { vm ->
                    vm.getUsers().whereEqualTo("email", targetEmail.toLowerCase(Locale.ROOT)).get().addOnSuccessListener {
                        it.documents.forEach { d ->
                            d.toObject(User::class.java)?.let { u ->
                                val discussionsRequestId = u.discussionsRequestId
                                if (discussionsRequestId.contains(discussionId)) {
                                    Toast.makeText(this, getString(R.string.requests_already_sent), Toast.LENGTH_SHORT).show()
                                    return@addOnSuccessListener
                                }
                                discussionsRequestId.add(discussionId)
                                vm.updateDiscussionsRequestUser(u.id, discussionsRequestId)
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

    private fun getUsers() {
        viewModel?.getUserFromDiscussion(discussionId)?.observe(this, androidx.lifecycle.Observer {
            it?.let { users ->
                usersInDiscussion.clear()
                adapter?.notifyDataSetChanged()
                usersInDiscussion.addAll(users)
                adapter?.notifyDataSetChanged()
            }
        })
    }

    override fun getLayoutId() = R.layout.activity_users

    override fun onClick(o: User) {}

    override fun onLongClick(o: User) {
        viewModel?.getDiscussion(discussionId)?.observe(this, androidx.lifecycle.Observer { g ->
            g?.let { discussion ->
                discussion.ownerId.takeIf { it != "" }?.let { userId ->
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
                    usersInDiscussion.clear()
                    adapter?.notifyDataSetChanged()
                    vm.getUser(user.id).observe(this, androidx.lifecycle.Observer {
                        it?.let { user ->
                            vm.getDiscussion(discussionId).observe(this, androidx.lifecycle.Observer { g ->
                                g?.let { discussion ->
                                    val discussionIds = user.discussionsId
                                    val userIds = discussion.usersId
                                    userIds.remove(user.id)
                                    discussionIds.remove(discussionId)
                                    vm.updateDiscussionIdsFromUser(user.id, discussionIds)
                                    vm.updateUserIdsFromDiscussion(discussionId, userIds)
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
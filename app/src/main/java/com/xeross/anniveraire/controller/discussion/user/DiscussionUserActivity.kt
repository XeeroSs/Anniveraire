package com.xeross.anniveraire.controller.discussion.user

import android.os.Bundle
import android.view.LayoutInflater
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.UserAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.activity_discussion_user.*
import kotlinx.android.synthetic.main.bsd_confirm.view.*

class DiscussionUserActivity : BaseActivity(), ClickListener<User> {

    private val usersInDiscussion = ArrayList<User>()
    private var viewModel: DiscussionUserViewModel? = null
    private var adapter: UserAdapter? = null
    private lateinit var discussionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(Constants.ID_DISCUSSION)?.let { s ->
            viewModel = configureViewModel()
            discussionId = s
            UserAdapter(usersInDiscussion, this, this).let {
                adapter = it
                activity_discussion_user_recyclerview.setRecyclerViewAdapter(it)
            }
            getUsers()
        } ?: finish()
    }

    private fun getUsers() {
        viewModel?.let { vm ->
            vm.getDiscussion(discussionId).addOnSuccessListener { dsD ->
                dsD.toObject(Discussion::class.java)?.let { d ->
                    d.usersId.forEach { userId ->
                        vm.getUser(userId).addOnSuccessListener { dsU ->
                            dsU.toObject(User::class.java)?.let { u ->
                                usersInDiscussion.add(u)
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getToolBar() = R.id.activity_discussion_user_toolbar
    override fun getLayoutId() = R.layout.activity_discussion_user

    override fun onClick(o: User) {}

    override fun onLongClick(o: User) {
        viewModel?.getDiscussion(discussionId)?.addOnSuccessListener { document ->
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
                    usersInDiscussion.clear()
                    adapter?.notifyDataSetChanged()
                    vm.getUser(user.id).addOnSuccessListener { d ->
                        d.toObject(User::class.java)?.let { u ->
                            val discussionIds = u.discussionsId ?: ArrayList()
                            discussionIds.remove(discussionId)
                            vm.updateDiscussionsUser(u.id, discussionIds)
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
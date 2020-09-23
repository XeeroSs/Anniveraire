package com.xeross.anniveraire.controller.discussion.user

import android.os.Bundle
import android.view.LayoutInflater
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.DiscussionUserAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.activity_discussion_user.*
import kotlinx.android.synthetic.main.bsd_confirm.view.*

class DiscussionUserActivity : BaseActivity(), ClickListener<User> {

    private val usersInDiscussion = ArrayList<User>()
    private var viewModel: DiscussionUserViewModel? = null
    private var adapter: DiscussionUserAdapter? = null
    private lateinit var discussionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(Constants.ID_DISCUSSION)?.let { s ->
            viewModel = configureViewModel()
            discussionId = s
            DiscussionUserAdapter(usersInDiscussion, this, this).let {
                adapter = it
                activity_discussion_user_recyclerview.setRecyclerViewAdapter(it)
            }
        } ?: finish()
    }


    override fun getToolBar() = R.id.activity_discussion_user_toolbar
    override fun getLayoutId() = R.layout.activity_discussion_user

    override fun onClick(o: User) {}

    override fun onLongClick(o: User) {
        confirm(o)
    }

    private fun confirm(user: User) {
        LayoutInflater.from(this).inflate(R.layout.bsd_item_delete, null).let {

            val bottomSheetDialog = createBSD(it)

            it.bsd_confirm_yes.setOnClickListener {
                // delete
                viewModel?.let { vm ->
                    vm.getUser(user.id)?.addOnSuccessListener { d ->
                        d.toObject(User::class.java)?.let { u ->
                            val discussionIds = u.discussionsId ?: ArrayList()
                            discussionIds.remove(discussionId)
                            vm.updateDiscussionsUser(u.id, discussionIds)
                        }
                    }
                }
                bottomSheetDialog.dismiss()
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }

    }

}
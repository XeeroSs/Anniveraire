package com.xeross.anniveraire.controller.discussion.request

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.RequestAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.listener.RequestListener
import com.xeross.anniveraire.model.Discussion
import kotlinx.android.synthetic.main.activity_discussion_request.*

// Activity grouping the user's discussion invitations
class DiscussionRequestActivity : BaseActivity(), RequestListener<Discussion> {

    private lateinit var viewModel: DiscussionRequestViewModel
    private var adapter: RequestAdapter<Discussion>? = null
    private val discussions = ArrayList<Discussion>()
    private lateinit var userId: String

    // Create activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = getCurrentUser()?.uid ?: return finish()
        viewModel = configureViewModel() ?: return finish()
        initializeRecyclerView()
        getDiscussionsFromUser()
    }

    // Initialize recyclerView
    private fun initializeRecyclerView() {
        RequestAdapter(this, discussions, this).let {
            adapter = it
            discussion_request_activity_recyclerview.setRecyclerViewAdapter(it)
        }
    }

    private fun getDiscussionsFromUser() {
        viewModel.getDiscussionsFromUser(userId).observe(this, Observer {
            it?.let {
                discussions.addAll(it)
                adapter?.notifyDataSetChanged()
            }
        })
    }

    override fun getLayoutId() = R.layout.activity_discussion_request
    override fun getToolBarTitle() = "Discussion requests"

    // When a user accepts a invitation
    override fun join(dObject: Discussion) {
        updateRecyclerView()
        viewModel.getUser(userId).observe(this, Observer {
            it?.let { user ->
                viewModel.joinDiscussion(dObject, userId, user.galleriesId)
                viewModel.removeDiscussionRequest(dObject, userId, user.galleriesRequestId)
                Toast.makeText(this, "Discussion join !", Toast.LENGTH_SHORT).show()
                getDiscussionsFromUser()
            }
        })
    }

    // When a user refuses an invitation
    override fun deny(dObject: Discussion) {
        updateRecyclerView()
        viewModel.getUser(userId).observe(this, Observer {
            it?.let { user ->
                viewModel.removeDiscussionRequest(dObject, userId, user.galleriesRequestId)
                Toast.makeText(this, "Discussion request delete !", Toast.LENGTH_SHORT).show()
                getDiscussionsFromUser()
            }
        })
    }

    // Update recyclerView
    private fun updateRecyclerView() {
        discussions.clear()
        adapter?.notifyDataSetChanged()
    }
}
package com.xeross.anniveraire.controller.discussion.request

import android.os.Bundle
import android.widget.Toast
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.RequestAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.listener.RequestContract
import com.xeross.anniveraire.listener.RequestListener
import com.xeross.anniveraire.model.Discussion
import kotlinx.android.synthetic.main.activity_discussion_request.*

// Activity grouping the user's discussion invitations
class DiscussionRequestActivity : BaseActivity(), RequestListener<Discussion>,
        RequestContract.View<Discussion> {

    private lateinit var presenter: DiscussionRequestPresenter
    private var adapter: RequestAdapter<Discussion>? = null
    private val discussions = ArrayList<Discussion>()
    private lateinit var userId: String

    // Create activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = getCurrentUser()?.uid ?: return finish()
        presenter = DiscussionRequestPresenter(this)
        initializeRecyclerView()
        presenter.getObjectsFromUser(userId)
    }

    override fun getRequests() {
        presenter.getObjectsFromUser(userId)
    }

    // Initialize recyclerView
    private fun initializeRecyclerView() {
        RequestAdapter(this, discussions, this).let {
            adapter = it
            discussion_request_activity_recyclerview.setRecyclerViewAdapter(it)
        }
    }

    override fun getLayoutId() = R.layout.activity_discussion_request
    override fun getToolBarTitle() = "Discussion requests"

    override fun getObjectsFromUser(tObjects: ArrayList<Discussion>) {
        this.discussions.clear()
        this.discussions.addAll(tObjects)
        adapter?.notifyDataSetChanged()
    }

    override fun setList() {
        discussions.clear()
        adapter?.notifyDataSetChanged()
    }

    // When a user accepts invitation
    override fun join(dObject: Discussion) {
        presenter.joinObject(dObject, userId)
        Toast.makeText(this, "Discussion join !", Toast.LENGTH_SHORT).show()
    }

    // When a user refuses invitation
    override fun deny(dObject: Discussion) {
        presenter.removeObjectRequest(dObject, userId)
        Toast.makeText(this, "Discussion request delete !", Toast.LENGTH_SHORT).show()
    }
}
package com.xeross.anniveraire.controller.discussion.request

import android.os.Bundle
import android.widget.Toast
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.DiscussionRequestAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.User
import kotlinx.android.synthetic.main.activity_discussion_request.*

class DiscussionRequestActivity : BaseActivity() {

    private var viewModel: DiscussionRequestViewModel? = null
    private var adapterEvent: DiscussionRequestAdapter? = null
    private val discussions = ArrayList<Discussion>()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = configureViewModel()
        DiscussionRequestAdapter(discussions, this).let {
            adapterEvent = it
            discussion_request_activity_recyclerview.setRecyclerViewAdapter(it)
        }
        userId = getCurrentUser()?.uid ?: return
        userId?.let { getDiscussionsFromUser(it) }
    }

    private fun getDiscussionsFromUser(userId: String) {
        viewModel?.let { vm ->
            vm.getUser(userId).addOnCompleteListener { taskUser ->
                taskUser.result?.toObject(User::class.java)?.let { user ->
                    user.discussionsRequestId.forEach { dId ->
                        vm.getDiscussions(dId).addOnCompleteListener { taskDiscussion ->
                            taskDiscussion.result?.toObject(Discussion::class.java)?.let { discussion ->
                                discussions.add(discussion)
                                adapterEvent?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    fun joinRequest(discussion: Discussion) {
        viewModel?.let { vm ->
            userId?.let {
                discussions.clear()
                adapterEvent?.notifyDataSetChanged()
                vm.getUser(it).addOnCompleteListener { t ->
                    t.result?.toObject(User::class.java)?.let { user ->
                        vm.updateDiscussionAndUser(discussion, it, user.discussionsId)
                        vm.discussionRequestRemove(discussion, it, user.discussionsRequestId)
                        Toast.makeText(this, "Discussion join !", Toast.LENGTH_SHORT).show()
                        getDiscussionsFromUser(it)
                    }
                }
            }
        }
    }

    fun deleteRequest(discussion: Discussion) {
        viewModel?.let { vm ->
            userId?.let {
                discussions.clear()
                adapterEvent?.notifyDataSetChanged()
                vm.getUser(it).addOnCompleteListener { t ->
                    t.result?.toObject(User::class.java)?.let { user ->
                        vm.discussionRequestRemove(discussion, it, user.discussionsRequestId)
                        Toast.makeText(this, "Discussion delete !", Toast.LENGTH_SHORT).show()
                        getDiscussionsFromUser(it)
                    }
                }
            }
        }
    }

    override fun getToolBar() = R.id.discussion_request_activity_toolbar

    override fun getLayoutId() = R.layout.activity_discussion_request
}
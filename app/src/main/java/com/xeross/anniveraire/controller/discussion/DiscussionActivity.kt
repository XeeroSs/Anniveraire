package com.xeross.anniveraire.controller.discussion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.DiscussionAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.controller.messages.MessageActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.ID_DISCUSSION
import kotlinx.android.synthetic.main.activity_discussion.*
import kotlinx.android.synthetic.main.bsd_discussion.view.*

class DiscussionActivity : BaseActivity(), ClickListener<Discussion> {

    private var viewModel: DiscussionViewModel? = null
    private var adapterEvent: DiscussionAdapter? = null
    private val discussions = ArrayList<Discussion>()

    private fun createBSDDiscussion() {
        LayoutInflater.from(this).inflate(R.layout.bsd_discussion, null).let { view ->
            val alertDialog = createBSD(view)

            view.bsd_discussion_button_add.setOnClickListener {
                if (view.bsd_discussion_edittext.text!!.isEmpty()) {
                    sendMissingInformationMessage()
                    return@setOnClickListener
                }

                val discussion = Discussion(name = view.bsd_discussion_edittext.text.toString())

                val userId = getCurrentUser()?.uid ?: return@setOnClickListener

                viewModel?.let { vm ->
                    vm.getUser(userId).addOnCompleteListener { t ->
                        t.result?.toObject(User::class.java)?.let { user ->
                            viewModel?.createDiscussion(discussion, userId, user.discussionsId)
                            Toast.makeText(this, "Discussion create !", Toast.LENGTH_SHORT).show()
                            discussions.clear()
                            getDiscussionsFromUser(userId)
                            alertDialog.dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun getToolBar() = R.id.toolbar

    override fun getLayoutId() = R.layout.activity_discussion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = configureViewModel()
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            createBSDDiscussion()
        }
        initializeRecyclerView()
        val userId = getCurrentUser()?.uid ?: return
        getDiscussionsFromUser(userId)
    }

    private fun initializeRecyclerView() {
        DiscussionAdapter(discussions, this, this).let {
            adapterEvent = it
            recyclerview_social.setRecyclerViewAdapter(it)
        }
    }

    private fun getDiscussionsFromUser(userId: String) {
        viewModel?.let { vm ->
            vm.getUser(userId).addOnCompleteListener { taskUser ->
                taskUser.result?.toObject(User::class.java)?.let { user ->
                    user.discussionsId?.forEach { dId ->
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

    override fun onClick(o: Discussion) {
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra(ID_DISCUSSION, o.id)
        startActivity(intent)
    }

    override fun onLongClick(o: Discussion) {
        TODO("Not yet implemented")
    }

}
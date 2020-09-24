package com.xeross.anniveraire.controller.discussion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.DiscussionAdapter
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.controller.messages.MessageActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.ID_DISCUSSION
import kotlinx.android.synthetic.main.activity_discussion.*
import kotlinx.android.synthetic.main.bsd_confirm.view.*
import kotlinx.android.synthetic.main.bsd_discussion.view.*
import kotlinx.android.synthetic.main.bsd_item_leave.view.*
import kotlinx.android.synthetic.main.bsd_item_selected.view.*

class DiscussionActivity : BaseActivity(), ClickListener<Discussion> {

    private var viewModel: DiscussionViewModel? = null
    private var adapterEvent: DiscussionAdapter? = null
    private val discussions = ArrayList<Discussion>()

    private fun createBSDDiscussion(discussionId: String?) {
        LayoutInflater.from(this).inflate(R.layout.bsd_discussion, null).let { view ->
            val alertDialog = createBSD(view)

            view.bsd_discussion_button_add.setOnClickListener {
                if (view.bsd_discussion_edittext.text!!.isEmpty()) {
                    sendMissingInformationMessage()
                    return@setOnClickListener
                }

                val userId = getCurrentUser()?.uid ?: return@setOnClickListener

                viewModel?.let { vm ->
                    if (discussionId == null) {
                        val discussion = Discussion(name = view.bsd_discussion_edittext.text.toString(), ownerId = userId)
                        vm.getUser(userId).addOnCompleteListener { t ->
                            t.result?.toObject(User::class.java)?.let { user ->
                                viewModel?.createDiscussion(discussion, userId, user.discussionsId)
                                Toast.makeText(this, "Discussion create !", Toast.LENGTH_SHORT).show()
                                discussions.clear()
                                getDiscussionsFromUser(userId)
                                alertDialog.dismiss()
                            }
                        }
                    } else {
                        vm.updateDiscussionName(view.bsd_discussion_edittext.text.toString(), discussionId)
                        Toast.makeText(this, "Name update !", Toast.LENGTH_SHORT).show()
                        discussions.clear()
                        getDiscussionsFromUser(userId)
                        alertDialog.dismiss()
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
        fab.setOnClickListener {
            createBSDDiscussion(null)
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
                        vm.getDiscussion(dId).addOnCompleteListener { taskDiscussion ->
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
        viewModel?.getDiscussion(o.id)?.addOnSuccessListener { document ->
            document.toObject(Discussion::class.java)?.let { d ->
                if (o.ownerId == getCurrentUser()?.uid) {
                    itemSelectedOwnerDiscussion(d)
                    return@addOnSuccessListener
                }
                itemSelected(d)
            }
        }
    }

    private fun itemSelected(discussion: Discussion) {
        LayoutInflater.from(this).inflate(R.layout.bsd_item_leave, null).run {

            val bottomSheetDialog = createBSD(this)

            bsd_item_selected_leave.setOnClickListener {
                confirmLeave(discussion)
                bottomSheetDialog.dismiss()
            }
        }

    }

    private fun itemSelectedOwnerDiscussion(discussion: Discussion) {
        LayoutInflater.from(this).inflate(R.layout.bsd_item_selected, null).run {

            val bottomSheetDialog = createBSD(this)

            bsd_item_selected_edit.setOnClickListener {
                createBSDDiscussion(discussion.id)
                bottomSheetDialog.dismiss()
            }
            bsd_item_selected_delete.setOnClickListener {
                confirmDelete(discussion)
                bottomSheetDialog.dismiss()
            }
        }

    }

    private fun confirmDelete(discussion: Discussion) {
        LayoutInflater.from(this).inflate(R.layout.bsd_confirm_deleted_permanently, null).let {

            val bottomSheetDialog = createBSD(it)

            val userUid = getCurrentUser()?.uid ?: return

            it.bsd_confirm_yes.setOnClickListener {
                // delete
                bottomSheetDialog.dismiss()
                viewModel?.deleteDiscussion(discussion.id)
                discussions.clear()
                getDiscussionsFromUser(userUid)
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }

    }

    private fun confirmLeave(discussion: Discussion) {
        LayoutInflater.from(this).inflate(R.layout.bsd_confirm_leave, null).let {

            val bottomSheetDialog = createBSD(it)

            it.bsd_confirm_yes.setOnClickListener {

                val userId = getCurrentUser()?.uid ?: return@setOnClickListener

                // delete
                bottomSheetDialog.dismiss()
                viewModel?.let { vm ->
                    vm.getUser(userId).addOnSuccessListener { d ->
                        d.toObject(User::class.java)?.let { u ->
                            val discussionIds = u.discussionsId ?: ArrayList()
                            vm.removeDiscussionAndUser(discussion, userId, discussionIds)
                            discussions.clear()
                            getDiscussionsFromUser(userId)
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
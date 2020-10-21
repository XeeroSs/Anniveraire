package com.xeross.anniveraire.controller.discussion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.DiscussionAdapter
import com.xeross.anniveraire.controller.base.BaseFragment
import com.xeross.anniveraire.controller.messages.MessageActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.bsd_confirm.view.*
import kotlinx.android.synthetic.main.bsd_discussion.view.*
import kotlinx.android.synthetic.main.bsd_item_leave.view.*
import kotlinx.android.synthetic.main.bsd_item_selected.view.*
import kotlinx.android.synthetic.main.fragment_discussions.*
import java.util.*
import kotlin.collections.ArrayList

class DiscussionsFragment : BaseFragment(), ClickListener<Discussion> {

    private var viewModel: DiscussionViewModel? = null
    private var adapter: DiscussionAdapter? = null
    private val discussions = ArrayList<Discussion>()
    private val discussionsFull = ArrayList<Discussion>()
    private lateinit var userId: String

    @SuppressLint("InflateParams")
    // Bottom sheet dialog for create a new discussion
    private fun createBSDDiscussion(discussionId: String?) {
        LayoutInflater.from(context).inflate(R.layout.bsd_discussion, null).let { view ->
            val alertDialog = createBSD(view)

            view.bsd_discussion_button_add.setOnClickListener {
                if (view.bsd_discussion_edittext.text!!.isEmpty()) {
                    sendMissingInformationMessage()
                    return@setOnClickListener
                }

                viewModel?.let { vm ->
                    alertDialog?.dismiss()
                    if (discussionId == null) {
                        val discussion = Discussion(name = view.bsd_discussion_edittext.text.toString(), ownerId = userId, activityDate = Date())
                        vm.getUser(userId).observe(this, androidx.lifecycle.Observer {
                            it?.let { user ->
                                vm.createDiscussion(discussion, userId, user.discussionsId)
                                Toast.makeText(context, "Discussion create !", Toast.LENGTH_SHORT).show()
                                updateRecyclerView()
                                getDiscussionsFromUser()
                            }
                        })
                        return@setOnClickListener
                    }
                    vm.updateDiscussionName(view.bsd_discussion_edittext.text.toString(), discussionId)
                    Toast.makeText(context, "Name update !", Toast.LENGTH_SHORT).show()
                    updateRecyclerView()
                    getDiscussionsFromUser()
                }
            }
        }
    }

    override fun getFragmentId() = R.layout.fragment_discussions
    override fun setFragment() = this

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = configureViewModel()
        initializeRecyclerView()
        userId = getCurrentUser()?.uid ?: return
    }

    // search discussion
    override fun onSearch(searchView: SearchView) {
        searchEvent(searchView)
    }

    // search discussion
    private fun searchEvent(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return true
            }

        })
    }

    // add discussion
    override fun onAdd() {
        createBSDDiscussion(null)
    }

    // initialize recyclerView
    private fun initializeRecyclerView() {
        context?.let { c ->
            DiscussionAdapter(discussions, discussionsFull, this, c).let {
                adapter = it
                fragment_discussion_recyclerview.setRecyclerViewAdapter(it)
            }
        }
    }

    // Get all discussions from user in firebase
    private fun getDiscussionsFromUser() {
        viewModel?.getDiscussionsFromUser(userId)?.observe(this, androidx.lifecycle.Observer {
            it?.let { list ->
                updateRecyclerView()
                discussions.addAll(list)
                discussionsFull.addAll(list)
                discussions.sortList()
                discussionsFull.sortList()
                adapter?.notifyDataSetChanged()
            }
        })
    }

    // Sort by date
    private fun ArrayList<Discussion>.sortList() {
        sortWith(Comparator { d1, d2 -> d1.activityDate.compareTo(d2.activityDate) })
        reverse()
    }

    override fun onStart() {
        super.onStart()
        updateRecyclerView()
        getDiscussionsFromUser()
    }

    // Long click on discussion item
    override fun onClick(o: Discussion) {
        val intent = Intent(context, MessageActivity::class.java)
        intent.putExtra(Constants.ID_DISCUSSION, o.id)
        startActivity(intent)
    }

    // Long click on discussion item
    override fun onLongClick(o: Discussion) {
        // Check if the user's discussion owner
        if (o.ownerId == userId) {
            itemSelectedOwnerDiscussion(o)
            return
        }
        itemSelected(o)
    }

    // Bottom sheet dialog -> item selected by user
    @SuppressLint("InflateParams")
    private fun itemSelected(discussion: Discussion) {
        LayoutInflater.from(context).inflate(R.layout.bsd_item_leave, null).run {

            val bottomSheetDialog = createBSD(this)

            bsd_item_selected_leave.setOnClickListener {
                confirmLeave(discussion)
                bottomSheetDialog?.dismiss()
            }
        }

    }

    // Bottom sheet dialog -> item selected by discussion owner
    @SuppressLint("InflateParams")
    private fun itemSelectedOwnerDiscussion(discussion: Discussion) {
        LayoutInflater.from(context).inflate(R.layout.bsd_item_selected, null).run {

            val bottomSheetDialog = createBSD(this)

            bsd_item_selected_edit.setOnClickListener {
                createBSDDiscussion(discussion.id)
                bottomSheetDialog?.dismiss()
            }
            bsd_item_selected_delete.setOnClickListener {
                confirmDelete(discussion)
                bottomSheetDialog?.dismiss()
            }
        }

    }

    // Bottom sheet dialog -> confirm delete discussion
    @SuppressLint("InflateParams")
    private fun confirmDelete(discussion: Discussion) {
        LayoutInflater.from(context).inflate(R.layout.bsd_confirm_deleted_permanently, null).let {

            val bottomSheetDialog = createBSD(it)

            it.bsd_confirm_yes.setOnClickListener {
                // delete
                bottomSheetDialog?.dismiss()
                viewModel?.deleteDiscussion(discussion.id)
                updateRecyclerView()
                getDiscussionsFromUser()
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog?.dismiss()
            }
        }

    }

    // Bottom sheet dialog -> confirm leave discussion
    @SuppressLint("InflateParams")
    private fun confirmLeave(discussion: Discussion) {
        LayoutInflater.from(context).inflate(R.layout.bsd_confirm_leave, null).let { view ->

            val bottomSheetDialog = createBSD(view)

            view.bsd_confirm_yes.setOnClickListener { _ ->
                // delete
                bottomSheetDialog?.dismiss()
                viewModel?.let { v ->
                    v.getDiscussion(discussion.id).observe(this, androidx.lifecycle.Observer {
                        it?.let { d ->
                            v.getUser(userId).observe(this, androidx.lifecycle.Observer { liveData ->
                                liveData?.let { user ->
                                    val discussionIds = user.discussionsId
                                    v.removeDiscussionFromUser(d, userId, discussionIds)
                                    updateRecyclerView()
                                    getDiscussionsFromUser()
                                }
                            })
                        }
                    })
                    return@setOnClickListener
                }
            }
            view.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog?.dismiss()
            }
        }

    }

    // Update recyclerView
    private fun updateRecyclerView() {
        discussions.clear()
        discussionsFull.clear()
        adapter?.notifyDataSetChanged()
    }

}

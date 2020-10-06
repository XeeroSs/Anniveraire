package com.xeross.anniveraire.controller.discussion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.DiscussionAdapter
import com.xeross.anniveraire.controller.BaseFragment
import com.xeross.anniveraire.controller.discussion.request.DiscussionRequestActivity
import com.xeross.anniveraire.controller.messages.MessageActivity
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
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

    private fun createBSDDiscussion(discussionId: String?) {
        LayoutInflater.from(context).inflate(R.layout.bsd_discussion, null).let { view ->
            val alertDialog = createBSD(view)

            view.bsd_discussion_button_add.setOnClickListener {
                if (view.bsd_discussion_edittext.text!!.isEmpty()) {
                    sendMissingInformationMessage()
                    return@setOnClickListener
                }

                val userId = getCurrentUser()?.uid ?: return@setOnClickListener

                viewModel?.let { vm ->
                    if (discussionId == null) {
                        val discussion = Discussion(name = view.bsd_discussion_edittext.text.toString(), ownerId = userId, activityDate = Date())
                        vm.getUser(userId).addOnCompleteListener { t ->
                            t.result?.toObject(User::class.java)?.let { user ->
                                viewModel?.createDiscussion(discussion, userId, user.discussionsId)
                                Toast.makeText(context, "Discussion create !", Toast.LENGTH_SHORT).show()
                                discussions.clear()
                                discussionsFull.clear()
                                getDiscussionsFromUser(userId)
                                alertDialog?.dismiss()
                            }
                        }
                    } else {
                        vm.updateDiscussionName(view.bsd_discussion_edittext.text.toString(), discussionId)
                        Toast.makeText(context, "Name update !", Toast.LENGTH_SHORT).show()
                        discussions.clear()
                        discussionsFull.clear()
                        getDiscussionsFromUser(userId)
                        alertDialog?.dismiss()
                    }
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
        val userId = getCurrentUser()?.uid ?: return
        getDiscussionsFromUser(userId)
    }

    override fun onRequest() {
        startActivity(Intent(activity, DiscussionRequestActivity::class.java))
    }

    override fun onSearch(searchView: SearchView) {
        searchEvent(searchView)
    }

    private fun searchEvent(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return true
            }

        })
    }

    override fun onAdd() {
        createBSDDiscussion(null)
    }

    private fun initializeRecyclerView() {
        context?.let { c ->
            DiscussionAdapter(discussions, discussionsFull, this, c).let {
                adapter = it
                fragment_discussion_recyclerview.setRecyclerViewAdapter(it)
            }
        }
    }

    private fun getDiscussionsFromUser(userId: String) {
        viewModel?.let { vm ->
            vm.getUser(userId).addOnCompleteListener { taskUser ->
                taskUser.result?.toObject(User::class.java)?.let { user ->
                    user.discussionsId.forEach { dId ->
                        vm.getDiscussion(dId).addOnCompleteListener { taskDiscussion ->
                            taskDiscussion.result?.toObject(Discussion::class.java)?.let { discussion ->
                                discussions.add(discussion)
                                discussionsFull.add(discussion)
                                discussions.sortList()
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun ArrayList<Discussion>.sortList() {
        sortWith(Comparator { d1, d2 ->
            d1.activityDate.compareTo(d2.activityDate);
        })
        reverse()
    }

    override fun onDestroy() {
        super.onDestroy()
        discussions.clear()
        discussionsFull.clear()
        adapter?.notifyDataSetChanged()
        val userId = getCurrentUser()?.uid ?: return
        getDiscussionsFromUser(userId)
    }

    override fun onClick(o: Discussion) {
        val intent = Intent(context, MessageActivity::class.java)
        intent.putExtra(Constants.ID_DISCUSSION, o.id)
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
        LayoutInflater.from(context).inflate(R.layout.bsd_item_leave, null).run {

            val bottomSheetDialog = createBSD(this)

            bsd_item_selected_leave.setOnClickListener {
                confirmLeave(discussion)
                bottomSheetDialog?.dismiss()
            }
        }

    }

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

    private fun confirmDelete(discussion: Discussion) {
        LayoutInflater.from(context).inflate(R.layout.bsd_confirm_deleted_permanently, null).let {

            val bottomSheetDialog = createBSD(it)

            val userUid = getCurrentUser()?.uid ?: return

            it.bsd_confirm_yes.setOnClickListener {
                // delete
                bottomSheetDialog?.dismiss()
                viewModel?.deleteDiscussion(discussion.id)
                discussions.clear()
                discussionsFull.clear()
                getDiscussionsFromUser(userUid)
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog?.dismiss()
            }
        }

    }

    private fun confirmLeave(discussion: Discussion) {
        LayoutInflater.from(context).inflate(R.layout.bsd_confirm_leave, null).let {

            val bottomSheetDialog = createBSD(it)

            it.bsd_confirm_yes.setOnClickListener {

                val userId = getCurrentUser()?.uid ?: return@setOnClickListener

                // delete
                bottomSheetDialog?.dismiss()
                viewModel?.let { vm ->
                    vm.getUser(userId).addOnSuccessListener { d ->
                        d.toObject(User::class.java)?.let { u ->
                            val discussionIds = u.discussionsId ?: ArrayList()
                            vm.removeDiscussionAndUser(discussion, userId, discussionIds)
                            discussions.clear()
                            discussionsFull.clear()
                            getDiscussionsFromUser(userId)
                        }
                    }
                }
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog?.dismiss()
            }
        }

    }

}

package com.xeross.anniveraire.controller.discussion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.DiscussionAdapter
import com.xeross.anniveraire.controller.login.LoginActivity
import com.xeross.anniveraire.controller.messages.MessageActivity
import com.xeross.anniveraire.injection.ViewModelFactory
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.ID_DISCUSSION
import kotlinx.android.synthetic.main.activity_discussion.*
import kotlinx.android.synthetic.main.bsd_discussion.view.*
import kotlinx.android.synthetic.main.fragment_event.*
import java.util.*
import kotlin.collections.ArrayList

class DiscussionActivity : AppCompatActivity(), ClickListener<Discussion> {

    private var viewModel: DiscussionViewModel? = null
    private var adapterEvent: DiscussionAdapter? = null
    private val discussions = ArrayList<Discussion>()

    private fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    private fun sendMissingInformationMessage() {
        Toast.makeText(
                this, getString(R.string.missing_information),
                Toast.LENGTH_SHORT
        ).show()
    }

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

    private fun createBSD(view: View) = BottomSheetDialog(this).apply {
        setContentView(view)
        show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion)
        if (getCurrentUser() == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            return
        }
        setSupportActionBar(findViewById(R.id.toolbar))
        configureViewModel<DiscussionViewModel>(ViewModelFactory(this))?.let {
            viewModel = it
        }
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            createBSDDiscussion()
        }
        initializeRecyclerView()
        val userId = getCurrentUser()?.uid ?: return
        getDiscussionsFromUser(userId)
    }

    // ViewModel for Fragment
    private inline fun <reified VM : ViewModel> configureViewModel(viewModelFactory: ViewModelFactory): VM? {
        return ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)
    }

    private fun initializeRecyclerView() {
        adapterEvent = DiscussionAdapter(discussions, this, this)
        recyclerview_social.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapterEvent
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
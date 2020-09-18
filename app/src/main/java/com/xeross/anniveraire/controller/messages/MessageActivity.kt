package com.xeross.anniveraire.controller.messages

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.MessageAdapter
import com.xeross.anniveraire.injection.ViewModelFactory
import com.xeross.anniveraire.model.Message
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.ID_DISCUSSION
import kotlinx.android.synthetic.main.message_activity.*


class MessageActivity : AppCompatActivity() {

    private var adapter: MessageAdapter? = null
    private var user: User? = null
    private lateinit var discussionId: String
    private var viewModel: MessageViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_activity)
        intent.getStringExtra(ID_DISCUSSION)?.let { s ->
            configureViewModel<MessageViewModel>(ViewModelFactory(this))?.let {
                viewModel = it
            }
            discussionId = s
            this.configureRecyclerView(s)
            this.getCurrentUserFromFirestore()
            this.onClickSendMessage()
        } ?: finish()
    }

    private fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    private fun onClickSendMessage() {
        activity_message_chat_send_button.setOnClickListener {
            activity_message_chat_message_edit_text.takeIf { !TextUtils.isEmpty(activity_message_chat_message_edit_text.text) }?.let {
                user?.let { u ->
                    viewModel?.createMessageForChat(it.text.toString(), discussionId, u)
                    this.activity_message_chat_message_edit_text.setText("")
                }
            }
        }
    }

    private fun getCurrentUserFromFirestore() {
        getCurrentUser()?.let { u ->
            viewModel?.getUser(u.uid)?.addOnSuccessListener {
                user = it.toObject(User::class.java)
            }
        }
    }

    // ViewModel for Fragment
    inline fun <reified VM : ViewModel> configureViewModel(viewModelFactory: ViewModelFactory): VM? {
        return ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)
    }

    private fun configureRecyclerView(discussionId: String) {
        viewModel?.let { vm ->
            activity_message_chat_recycler_view.run {
                getCurrentUser()?.let { u ->
                    adapter = MessageAdapter(generateOptionsForAdapter(vm.getAllMessageForChat(discussionId)), Glide.with(this), u.uid).also {
                        it.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                                smoothScrollToPosition(it.itemCount) // Scroll to bottom on new messages
                            }
                        });
                        layoutManager = LinearLayoutManager(this@MessageActivity)
                        this.adapter = it
                    }
                }
            }
        }
    }

    private fun generateOptionsForAdapter(query: Query) = FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java)
            .setLifecycleOwner(this)
            .build()
}
package com.xeross.anniveraire.controller.messages

import android.os.Bundle
import android.os.PersistableBundle
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
import kotlinx.android.synthetic.main.message_activity.*


class MessageActivity : AppCompatActivity() {

    private var adapter: MessageAdapter? = null
    private var user: User? = null
    private var viewModel: MessageViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.message_activity)
        configureViewModel<MessageViewModel>(ViewModelFactory(this))?.let {
            viewModel = it
        }
        this.configureRecyclerView("")
        this.getCurrentUserFromFirestore()
    }

    private fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
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

    private fun configureRecyclerView(chatName: String) {
        viewModel?.let { vm ->
            activity_message_chat_recycler_view.run {
                getCurrentUser()?.let {
                    adapter = MessageAdapter(generateOptionsForAdapter(vm.getAllMessageForChat("test")), Glide.with(this), it.uid).also {
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
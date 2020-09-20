package com.xeross.anniveraire.controller.messages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.MessageAdapter
import com.xeross.anniveraire.injection.ViewModelFactory
import com.xeross.anniveraire.model.Message
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.ID_DISCUSSION
import kotlinx.android.synthetic.main.message_activity.*
import permissions.dispatcher.*
import java.util.*

@RuntimePermissions
class MessageActivity : AppCompatActivity() {

    companion object {
        const val RC_CHOOSE_PHOTO = 1
    }

    private var adapter: MessageAdapter? = null
    private var user: User? = null
    private lateinit var discussionId: String
    private var viewModel: MessageViewModel? = null
    private var uriImageSelected: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_activity)
        setSupportActionBar(message_activity_toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
        intent.getStringExtra(ID_DISCUSSION)?.let { s ->
            configureViewModel<MessageViewModel>(ViewModelFactory(this))?.let {
                viewModel = it
            }
            discussionId = s
            this.configureRecyclerView(s)
            this.getCurrentUserFromFirestore()
            this.onClickSendMessage()
            this.onClickSendImage()
        } ?: finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    private fun onClickSendMessage() {
        activity_message_chat_send_button.setOnClickListener {
            activity_message_chat_message_edit_text.takeIf { !TextUtils.isEmpty(activity_message_chat_message_edit_text.text) }?.let {
                user?.let { u ->
                    if (activity_message_chat_image_chosen_preview.drawable == null) {
                        // SEND A TEXT MESSAGE
                        viewModel?.createMessageForChat(it.text.toString(), discussionId, u)
                        it.setText("")
                    } else {
                        // SEND A IMAGE + TEXT IMAGE
                        this.uploadPhotoInFirebaseAndSendMessage(it.text.toString(), discussionId, u)
                        it.setText("")
                        activity_message_chat_image_chosen_preview.setImageDrawable(null)
                    }
                }
            }
        }
    }

    private fun uploadPhotoInFirebaseAndSendMessage(message: String, discussionId: String, user: User) {
        val uuid = UUID.randomUUID().toString() // GENERATE UNIQUE STRING
        // UPLOAD TO GCS
        val imageRef = FirebaseStorage.getInstance().getReference(uuid)
        uriImageSelected?.let { uri ->
            imageRef.putFile(uri).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { pathImageSavedInFirebase ->
                    // SAVE MESSAGE IN FIRESTORE
                    viewModel?.createMessageForChat(pathImageSavedInFirebase.toString(), message,
                            discussionId, user)
                }
            }
        }
    }

    private fun onClickSendImage() {
        activity_message_chat_add_file_button.setOnClickListener {
            showGalleryWithPermissionCheck()
        }
    }

    private fun getCurrentUserFromFirestore() {
        getCurrentUser()?.let { u ->
            viewModel?.getUser(u.uid)?.addOnSuccessListener {
                user = it.toObject(User::class.java)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleResponse(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
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

    @NeedsPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showGallery() {
        // 3 - Launch an "Selection Image" Activity
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RC_CHOOSE_PHOTO)
    }

    // 4 - Handle activity response (after user has chosen or not a picture)
    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                data?.let {
                    this.uriImageSelected = it.data
                    Glide.with(this) //SHOWING PREVIEW OF IMAGE
                            .load(this.uriImageSelected)
                            .apply(RequestOptions.circleCropTransform())
                            .into(this.activity_message_chat_image_chosen_preview)
                }
                return
            } else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @OnShowRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showRationaleForPermission(request: PermissionRequest) {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.need_permission))
                .setMessage(getString(R.string.missing_permission))
                .setPositiveButton(android.R.string.yes) { _, _ -> request.proceed() }
                .setNegativeButton(android.R.string.no) { _, _ -> request.cancel() }
                .show()
    }

    @OnPermissionDenied(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onPermissionDenied() {
        Toast.makeText(this, getString(R.string.missing_permission), Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onPermissionNeverAskAgain() {
        Toast.makeText(this, getString(R.string.missing_permission), Toast.LENGTH_SHORT).show()
    }

    private fun generateOptionsForAdapter(query: Query) = FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java)
            .setLifecycleOwner(this)
            .build()
}
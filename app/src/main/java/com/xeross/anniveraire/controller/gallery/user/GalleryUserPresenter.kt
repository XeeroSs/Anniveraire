package com.xeross.anniveraire.controller.gallery.user

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.xeross.anniveraire.R
import com.xeross.anniveraire.listener.UserContract
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.GALLERY_COLLECTION
import com.xeross.anniveraire.utils.Constants.USERS_COLLECTION

class GalleryUserPresenter(private val contract: UserContract.View) :
        UserContract.Presenter {

    private val databaseGalleryInstance =
            FirebaseFirestore.getInstance().collection(GALLERY_COLLECTION)
    private val databaseUsersInstance =
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    private fun getDocumentUser(id: String) = databaseUsersInstance.document(id).get()
    private fun getDocumentGallery(id: String) = databaseGalleryInstance.document(id).get()
   private fun getUsers() = databaseUsersInstance

    private fun updateGalleryIdsFromUser(id: String, galleriesId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("galleriesId", galleriesId)

    }

    private fun updateUserIdsFromGallery(id: String, usersId: ArrayList<String>) {
        databaseGalleryInstance.document(id).update("usersId", usersId)
    }

    private fun updateGalleriesRequestUser(id: String, galleriesRequestId: ArrayList<String>) {
        databaseUsersInstance.document(id).update("galleriesRequestId", galleriesRequestId)
    }

    override fun getObjectsFromUser(id: String) {
        contract.setList()
        val users = ArrayList<User>()
        getDocumentGallery(id).addOnCompleteListener { taskGallery ->
            taskGallery.result?.toObject(Gallery::class.java)?.let { gallery ->
                gallery.usersId.forEach { uId ->
                    getDocumentUser(uId).addOnCompleteListener { taskUser ->
                        taskUser.result?.toObject(User::class.java)?.let { user ->
                            if (!users.contains(user)) {
                                users.add(user)
                                contract.getUsersFromObject(users)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun removeUser(userId: String, id: String) {
        contract.setList()
        getDocumentUser(userId).addOnCompleteListener { taskUser ->
            taskUser.result?.toObject(User::class.java)?.let { user ->
                getDocumentGallery(id).addOnCompleteListener { taskGallery ->
                    taskGallery.result?.toObject(Gallery::class.java)?.let { gallery ->
                        val galleriesId = user.galleriesId
                        val userIds = gallery.usersId
                        userIds.remove(user.id)
                        galleriesId.remove(id)
                        updateGalleryIdsFromUser(user.id, galleriesId)
                        updateUserIdsFromGallery(id, userIds)
                        contract.getUsers()
                    }
                }
            }
        }
    }

    override fun longClick(id: String, userId: String, targetId: String) {
        getDocumentGallery(id).addOnCompleteListener { td ->
            td.result?.toObject(Gallery::class.java)?.let { gallery ->
                gallery.ownerId.takeIf { it != "" }?.let { uId ->
                    if (uId == userId) {
                        if (targetId == userId) return@addOnCompleteListener
                        contract.showPopupConfirmSuppress(targetId)
                    }
                }
            }
        }
    }

    override fun sendRequestByEmail(id: String, email: String, alertDialog: BottomSheetDialog) {
        getUsers().whereEqualTo("email", email).get().addOnSuccessListener {
            it.documents.forEach { d ->
                d.toObject(User::class.java)?.let { u ->
                    val galleriesRequestId = u.galleriesRequestId
                    if (galleriesRequestId.contains(id)) {
                        contract.sendToast(R.string.requests_already_sent)
                        return@addOnSuccessListener
                    }
                    galleriesRequestId.add(id)
                    updateGalleriesRequestUser(u.id, galleriesRequestId)
                    contract.sendToast(R.string.request_sent)
                    alertDialog.dismiss()
                    return@addOnSuccessListener
                }
            }
            contract.sendToast(R.string.error_email_not_found)
        }.addOnFailureListener {
            contract.sendToast(R.string.error_email_not_found)
        }
    }

    override fun isOwnerUser(id: String, userId: String) {
        getDocumentGallery(id).addOnCompleteListener { task ->
            task.result?.toObject(Gallery::class.java)?.let { gallery ->
                gallery.ownerId.takeIf { it != "" }?.let { uId ->
                    if (uId == userId) {
                        contract.showPopupAddUser()
                        return@addOnCompleteListener
                    }
                }
            }
            contract.sendToast(R.string.you_cannot_add_anyone)
        }
    }
}

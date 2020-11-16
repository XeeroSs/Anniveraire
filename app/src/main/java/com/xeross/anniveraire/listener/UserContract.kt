package com.xeross.anniveraire.listener

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xeross.anniveraire.model.User

interface UserContract {
    interface View {
        fun setList()
        fun showPopupConfirmSuppress(userId: String)
        fun showPopupAddUser()
        fun getUsersFromObject(tObjects: ArrayList<User>)
        fun getUsers()
        fun sendToast(idText: Int)
    }

    interface Presenter {
        fun getObjectsFromUser(id: String)
        fun removeUser(userId: String, id: String)
        fun longClick(id: String, userId: String, targetId: String)
        fun sendRequestByEmail(id: String, email: String, alertDialog: BottomSheetDialog)
        fun isOwnerUser(id: String, userId: String)
    }
}
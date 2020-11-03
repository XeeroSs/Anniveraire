package com.xeross.anniveraire.controller.login

import com.xeross.anniveraire.model.User

interface LoginContract {
    interface Presenter {
        fun getUser(userId:String)
        fun createUser(uid: String, email: String?,
                       username: String?,
                       urlPicture: String?,
                       discussionId: ArrayList<String>, discussionRequestId: ArrayList<String>,
                       galleriesId: ArrayList<String>, galleriesRequestId: ArrayList<String>)
    }
    interface View {
        fun getUser(user: User?)
    }
}
package com.xeross.anniveraire.controller.discussion

import com.xeross.anniveraire.model.Discussion

interface DiscussionsContract {
    interface View {
        fun removeDiscussions()
        fun getDiscussions(tObjects: ArrayList<Discussion>)
        fun getDiscussions()
    }

    interface Presenter {
        fun getDiscussions(userId: String)
        fun addDiscussion(discussion: Discussion, userId: String)
        fun deleteDiscussion(id: String, userId: String)
        fun leaveDiscussion(id: String, userId: String)
        fun updateDiscussionName(id: String, newName: String)
    }
}
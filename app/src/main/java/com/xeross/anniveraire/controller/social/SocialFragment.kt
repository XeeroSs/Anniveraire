package com.xeross.anniveraire.controller.social

import android.content.Intent
import android.os.Bundle
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.BaseFragment
import com.xeross.anniveraire.controller.discussion.DiscussionActivity
import kotlinx.android.synthetic.main.fragment_social.*


class SocialFragment : BaseFragment() {

    override fun getFragmentId() = R.layout.fragment_social
    override fun setFragment() = this

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeOnClick()
    }

    private fun initializeOnClick() {
        fragment_social_login_google.setOnClickListener {
            val intent = Intent(context, DiscussionActivity::class.java)
            startActivity(intent)
        }
    }
}

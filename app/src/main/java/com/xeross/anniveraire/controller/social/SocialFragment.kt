package com.xeross.anniveraire.controller.social

import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.BaseFragment

class SocialFragment : BaseFragment() {

    private lateinit var socialViewModel: SocialViewModel
    override fun getFragmentId() = R.layout.fragment_social
    override fun setFragment() = this
}

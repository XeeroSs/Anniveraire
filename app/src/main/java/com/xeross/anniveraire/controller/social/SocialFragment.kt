package com.xeross.anniveraire.controller.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xeross.anniveraire.R

class SocialFragment : Fragment() {

    private lateinit var socialViewModel: SocialViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_social, container, false)
    }
}

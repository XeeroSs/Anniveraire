package com.xeross.anniveraire.controller.date

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.BaseFragment

class DateFragment : BaseFragment() {

    private lateinit var dateModel: DateModel

    override fun getFragmentId() = R.layout.fragment_date

}

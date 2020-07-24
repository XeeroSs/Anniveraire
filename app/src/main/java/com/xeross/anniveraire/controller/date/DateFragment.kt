package com.xeross.anniveraire.controller.date

import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.BaseEventFragment

class DateFragment : BaseEventFragment() {

    private lateinit var dateModel: DateModel

    override fun getFragmentId() = R.layout.fragment_date
    override fun setFragment() = this

}

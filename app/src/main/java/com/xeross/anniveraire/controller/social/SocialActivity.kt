package com.xeross.anniveraire.controller.social

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.BirthdayAdapter
import com.xeross.anniveraire.model.Birthday
import kotlinx.android.synthetic.main.fragment_event.*

class SocialActivity : AppCompatActivity() {

    private var adapterEvent: BirthdayAdapter? = null
    private val birthdays = ArrayList<Birthday>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)
    }

    private fun initializeRecyclerView() {
           // adapterEvent = BirthdayAdapter(birthdays, birthdaysFull, it, this.getDateToday(), this)
            fragment_event_list.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                itemAnimator = DefaultItemAnimator()
                adapter = adapterEvent
            }
    }

}
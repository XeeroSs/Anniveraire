package com.xeross.anniveraire.controller.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeross.anniveraire.R
import com.xeross.anniveraire.Utils
import com.xeross.anniveraire.adapter.EventAdapter
import com.xeross.anniveraire.model.Event
import com.xeross.anniveraire.model.EventState
import kotlinx.android.synthetic.main.fragment_event.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class EventFragment : Fragment() {

    //private lateinit var eventViewModel: EventViewModel
    private var root: View? = null
    private var adapterEvent: EventAdapter? = null
    private var events: ArrayList<Event>? = null
    private lateinit var dateToday: Date

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_event, container, false)
        dateToday = Date()
        /*  eventViewModel =
                  ViewModelProviders.of(this).get(EventViewModel::class.java)
          val textView: TextView = root.findViewById(R.id.text_home)
          eventViewModel.text.observe(viewLifecycleOwner, Observer {
              textView.text = it
          })*/
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeRecyclerView()
        events?.add(Event("Quentin", "Masini",
                Date(101, 7, 14),
                ""))
        events?.add(Event("Laurent", "Paul",
                Date(91, 11, 1),
                ""))
        events?.add(Event("Bob", "Jean",
                Date(110, 3, 22),
                ""))
        events?.add(Event("Noël",
                dateBirth = Date(0, 11, 25),
                imageURL = "https://i.pinimg.com/originals/15/05/a7/1505a796ee38433c10e9dca2db9e3a60.jpg",
                state = EventState.OTHER))
        events?.add(Event("Vacance",
                dateBirth = Date(118, 0, 30),
                imageURL = "https://i.pinimg.com/originals/15/05/a7/1505a796ee38433c10e9dca2db9e3a60.jpg",
                state = EventState.EVENT_BIRTHDAY))

        events?.sortWith(Comparator { o1, o2 ->
            (Utils.getRemainingDays(o1.dateBirth, dateToday) - Utils.getRemainingDays(o2.dateBirth, dateToday)).toInt()
        })

        adapterEvent?.notifyDataSetChanged()
    }

    private fun initializeRecyclerView() {
        events = ArrayList()
        adapterEvent = EventAdapter(context, events, dateToday)
        fragment_event_list.apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = adapterEvent
        }
    }
}

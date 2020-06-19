package com.xeross.anniveraire.controller.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xeross.anniveraire.R

class EventFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*  eventViewModel =
                  ViewModelProviders.of(this).get(EventViewModel::class.java)
          val textView: TextView = root.findViewById(R.id.text_home)
          eventViewModel.text.observe(viewLifecycleOwner, Observer {
              textView.text = it
          })*/
        return inflater.inflate(R.layout.fragment_event, container, false)
    }
}

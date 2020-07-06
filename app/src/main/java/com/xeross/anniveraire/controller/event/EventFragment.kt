package com.xeross.anniveraire.controller.event

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeross.anniveraire.R
import com.xeross.anniveraire.UtilsDate
import com.xeross.anniveraire.adapter.EventAdapter
import com.xeross.anniveraire.controller.BaseFragment
import com.xeross.anniveraire.model.Event
import com.xeross.anniveraire.model.EventState
import com.xeross.anniveraire.model.SortState
import kotlinx.android.synthetic.main.alertdialog_birthday.view.*
import kotlinx.android.synthetic.main.alertdialog_event_and_other.view.*
import kotlinx.android.synthetic.main.fragment_event.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class EventFragment : BaseFragment() {

    //private lateinit var eventViewModel: EventViewModel
    private var adapterEvent: EventAdapter? = null
    private var events: ArrayList<Event>? = null
    private var sortBy: SortState = SortState.DAY_REMAINING

    override fun getFragmentId() = R.layout.fragment_event

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.setFragment(this)
        initializeRecyclerView()
        events?.run {
            add(Event("Quentin", "Masini",
                    Date(101, 5, 14),
                    imageURL = "https://i.pinimg.com/originals/15/05/a7/1505a796ee38433c10e9dca2db9e3a60.jpg"))
            add(Event("Laurent", "Paul",
                    Date(109, 11, 1),
                    imageURL = "https://i.pinimg.com/originals/15/05/a7/1505a796ee38433c10e9dca2db9e3a60.jpg"))
            add(Event("Bob", "Jean",
                    Date(110, 3, 22),
                    imageURL = "https://i.pinimg.com/originals/15/05/a7/1505a796ee38433c10e9dca2db9e3a60.jpg"))
            add(Event("Noël",
                    dateBirth = Date(0, 11, 25),
                    imageURL = "https://i.pinimg.com/originals/15/05/a7/1505a796ee38433c10e9dca2db9e3a60.jpg",
                    state = EventState.OTHER))
            add(Event("Vacance",
                    dateBirth = Date(118, 0, 30),
                    imageURL = "https://i.pinimg.com/originals/15/05/a7/1505a796ee38433c10e9dca2db9e3a60.jpg",
                    state = EventState.EVENT_BIRTHDAY))
        }

        sortList()
    }

    private fun setSortBy(stateSort: SortState) {
        this.sortBy = stateSort
    }

    private fun sortList() {
        events?.sortWith(Comparator { event1, event2 ->
            when (sortBy) {
                SortState.DAY_REMAINING -> (UtilsDate.getRemainingDays(event1.dateBirth, this.getDateToday())
                        - UtilsDate.getRemainingDays(event2.dateBirth, this.getDateToday())).toInt()
                SortState.NAME -> "${event1.firstName} ${event1.lastName}".compareTo("${event2.firstName} ${event2.lastName}")
                SortState.AGE_DESCENDING -> {
                    compareAgeAscending(event1, event2)
                }
            }
        })
        adapterEvent?.notifyDataSetChanged()
    }

    private fun compareAgeAscending(event1: Event, event2: Event): Int {
        val ageEvent1 = UtilsDate.getAgeEvent(this.getDateToday(), event1.dateBirth).plus(1)
        val ageEvent2 = UtilsDate.getAgeEvent(this.getDateToday(), event2.dateBirth).plus(1)
        return ageEvent1.takeIf { it == ageEvent2 }?.let {
            (UtilsDate.getRemainingDays(event1.dateBirth, this.getDateToday())
                    - UtilsDate.getRemainingDays(event2.dateBirth, this.getDateToday())).toInt()
        } ?: (ageEvent1 - ageEvent2)
    }

    private fun updateEventList(event: Event) {
        events?.add(event)
        sortList()
    }

    private fun initializeRecyclerView() {
        events = ArrayList()
        adapterEvent = EventAdapter(context, events, this.getDateToday())
        fragment_event_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = adapterEvent
        }
    }

    internal fun onClickChoiceSort(alertDialog: AlertDialog, sortState: SortState) {
        setSortBy(sortState)
        sortList()
        alertDialog.dismiss()
    }

    internal fun createPopupForBirthday(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.alertdialog_birthday, null).let { view ->

            datePickerDialog = getDatePicker(view.alertdialog_birthday_edittext_date)
            onClickDatePicker(view.alertdialog_birthday_edittext_date, context)

            val alertDialog = createDialog(context, view, "Birthday")

            view.alertdialog_birthday_button_back.setOnClickListener {
                createPopupForChoiceEvent(context)
                alertDialog.dismiss()
            }
            view.alertdialog_birthday_button_add.setOnClickListener {
                if (view.alertdialog_birthday_edittext_date.text!!.isEmpty() ||
                        view.alertdialog_birthday_edittext_lastname.text!!.isEmpty() ||
                        view.alertdialog_birthday_edittext_name.text!!.isEmpty()) {
                    main.sendMissingInformationMessage()
                    return@setOnClickListener
                }

                val event = Event(firstName = view.alertdialog_birthday_edittext_name.text!!.toString(),
                        lastName = view.alertdialog_birthday_edittext_lastname.text!!.toString(),
                        dateBirth = UtilsDate.getStringInDate(view.alertdialog_birthday_edittext_date.text!!.toString()))

                updateEventList(event)
                alertDialog.dismiss()
            }
        }
    }

    internal fun createPopupForBirthdayEventOrOther(context: Context, isOther: Boolean) {
        LayoutInflater.from(context).inflate(R.layout.alertdialog_event_and_other, null).let { view ->

            datePickerDialog = getDatePicker(view.alertdialog_event_and_other_edittext_date)
            onClickDatePicker(view.alertdialog_event_and_other_edittext_date, context)

            val alertDialog = createDialog(context, view,
                    if (!isOther) "Event birthday" else "Event other")

            view.alertdialog_event_and_other_button_back.setOnClickListener {
                createPopupForChoiceEvent(context)
                alertDialog.dismiss()
            }

            view.alertdialog_event_and_other_button_add.setOnClickListener {
                if (view.alertdialog_event_and_other_edittext_date.text!!.isEmpty() ||
                        view.alertdialog_event_and_other_name.text!!.isEmpty()) {
                    main.sendMissingInformationMessage()
                    return@setOnClickListener
                }

                val event = if (!isOther) {
                    Event(firstName = view.alertdialog_event_and_other_name.text!!.toString(),
                            state = EventState.EVENT_BIRTHDAY,
                            dateBirth = UtilsDate.getStringInDate(view.alertdialog_event_and_other_edittext_date.text!!.toString()))
                } else {
                    Event(firstName = view.alertdialog_event_and_other_name.text!!.toString(),
                            state = EventState.OTHER,
                            dateBirth = UtilsDate.getStringInDate(view.alertdialog_event_and_other_edittext_date.text!!.toString()))
                }

                updateEventList(event)
                alertDialog.dismiss()
            }
        }
    }
}

package com.xeross.anniveraire.controller.event

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.BirthdayAdapter
import com.xeross.anniveraire.controller.BaseEventFragment
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Birthday
import com.xeross.anniveraire.model.BirthdayState
import com.xeross.anniveraire.model.SortState
import com.xeross.anniveraire.utils.UtilsDate
import kotlinx.android.synthetic.main.fragment_event.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class BirthdayFragment : BaseEventFragment(), ClickListener<Birthday> {

    private var adapterEvent: BirthdayAdapter? = null
    private val birthdays = ArrayList<Birthday>()
    private val birthdaysFull = ArrayList<Birthday>()
    private var sortBy: SortState = SortState.DAY_REMAINING

    override fun getFragmentId() = R.layout.fragment_event
    override fun setFragment() = this

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeRecyclerView()
        // TEST
        birthdays.run {
            addBirthday("Quentin", "Masini", Date(101, 5, 14), BirthdayState.BIRTHDAY)
            addBirthday("1", "2", Date(109, 11, 1), BirthdayState.BIRTHDAY)
            addBirthday("3", "4", Date(110, 3, 22), BirthdayState.BIRTHDAY)
            addBirthday("Noël", "", Date(0, 11, 25), BirthdayState.OTHER)
            // Sort List (Do stuff with room..)
            sortListWith()
        }
        birthdaysFull.addAll(birthdays)
    }

    private fun ArrayList<Birthday>.addBirthday(
            firstName: String,
            lastName: String,
            date: Date,
            birthdayState: BirthdayState
    ): Boolean {
        return add(Birthday(firstName, lastName, date, "", birthdayState))
    }

    internal fun getList() = birthdays

    private fun setSortBy(stateSort: SortState) {
        this.sortBy = stateSort
    }

    internal fun getAdapter() = adapterEvent

    fun sortList() {
        birthdays.run {
            clear()
            addAll(birthdaysFull)
            sortListWith()
            adapterEvent?.notifyDataSetChanged()
        }
    }

    private fun ArrayList<Birthday>.sortListWith() {
        sortWith(Comparator { event1, event2 ->
            when (sortBy) {
                SortState.DAY_REMAINING -> (UtilsDate.getRemainingDays(
                        getDateToday(),
                        event1.dateBirth
                ) - UtilsDate.getRemainingDays(getDateToday(), event2.dateBirth))
                SortState.NAME -> "${event1.firstName} ${event1.lastName}".compareTo("${event2.firstName} ${event2.lastName}")
                SortState.AGE_DESCENDING -> {
                    compareAgeAscending(event1, event2)
                }
            }
        })
    }

    private fun compareAgeAscending(birthday1: Birthday, birthday2: Birthday): Int {
        val ageEvent1 = UtilsDate.getAgeEvent(this.getDateToday(), birthday1.dateBirth).plus(1)
        val ageEvent2 = UtilsDate.getAgeEvent(this.getDateToday(), birthday2.dateBirth).plus(1)
        return ageEvent1.takeIf { it == ageEvent2 }?.let {
            (UtilsDate.getRemainingDays(this.getDateToday(), birthday1.dateBirth)
                    - UtilsDate.getRemainingDays(this.getDateToday(), birthday2.dateBirth))
        } ?: (ageEvent1 - ageEvent2)
    }

    fun updateEventList(birthday: Birthday) {
        birthdaysFull.add(birthday)
        sortList()
    }

    private fun initializeRecyclerView() {
        context?.let {
            adapterEvent = BirthdayAdapter(birthdays, birthdaysFull, it, this.getDateToday(), this)
            fragment_event_list.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                itemAnimator = DefaultItemAnimator()
                adapter = adapterEvent
            }
        }
    }

    internal fun onClickChoiceSort(bottomSheetDialog: BottomSheetDialog, sortState: SortState) {
        getEventFragment()?.setSortBy(sortState)
        getEventFragment()?.sortList()
        bottomSheetDialog.dismiss()
    }

    override fun onClick(o: Birthday) {
        /**Nothing**/
    }

    override fun onLongClick(o: Birthday) {
        context?.let { getBSDHelper()?.itemSelected(o) }
    }

}

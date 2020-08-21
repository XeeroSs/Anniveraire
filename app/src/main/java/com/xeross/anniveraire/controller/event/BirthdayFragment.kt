package com.xeross.anniveraire.controller.event

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.BirthdayAdapter
import com.xeross.anniveraire.controller.BaseEventFragment
import com.xeross.anniveraire.injection.ViewModelFactory
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Birthday
import com.xeross.anniveraire.model.SortState
import com.xeross.anniveraire.utils.UtilsDate
import kotlinx.android.synthetic.main.fragment_event.*


class BirthdayFragment : BaseEventFragment<BirthdayViewModel>(), ClickListener<Birthday> {

    private var adapterEvent: BirthdayAdapter? = null
    private val birthdays = ArrayList<Birthday>()
    private val birthdaysFull = ArrayList<Birthday>()
    private var sortBy: SortState = SortState.DAY_REMAINING

    override fun getFragmentId() = R.layout.fragment_event
    override fun setFragment() = this

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeRecyclerView()
        context?.let { context ->
            configureViewModel<BirthdayViewModel>(ViewModelFactory(context))?.let {
                viewModel = it
                getBirthdaysFromRoom(it)
            }
        }
    }

    private fun getBirthdaysFromRoom(it: BirthdayViewModel) {
        it.getBirthdays()?.observe(viewLifecycleOwner, Observer { birthdayList ->
            birthdayList?.let { list ->
                birthdays.run {
                    addAll(list)
                    sortListWith()
                    birthdaysFull.addAll(this)
                    adapterEvent?.notifyDataSetChanged()
                }
            }
        })
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
        viewModel?.let {
            it.createBirthday(birthday)
            birthdaysFull.add(birthday)
            sortList()
        }
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
        context?.let {
            viewModel?.let {
                it.getBirthday(o.id)?.observe(this, Observer { birthday ->
                    birthday?.let { b ->
                        getBSDHelper()?.itemSelected(b, it)
                    }
                })
            }
        }
    }

}

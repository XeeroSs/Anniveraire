package com.xeross.anniveraire.controller.event

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.xeross.anniveraire.R
import com.xeross.anniveraire.adapter.BirthdayAdapter
import com.xeross.anniveraire.controller.BaseFragment
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.listener.ToolBarListener
import com.xeross.anniveraire.model.Birthday
import com.xeross.anniveraire.utils.BottomSheetDialogHelper
import com.xeross.anniveraire.utils.UtilsDate
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.fragment_event.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class BirthdayFragment : BaseFragment(), ClickListener<Birthday> {

    private var adapterEvent: BirthdayAdapter? = null
    private val birthdays = ArrayList<Birthday>()
    private val birthdaysFull = ArrayList<Birthday>()
    private var viewModel: BirthdayViewModel? = null
    private lateinit var calendar: Calendar
    private lateinit var dateToday: Date
    private var bsdHelper: BottomSheetDialogHelper? = null
    internal lateinit var datePickerDialog: DatePickerDialog.OnDateSetListener

    fun getList() = birthdays
    fun getAdapter() = adapterEvent
    override fun getFragmentId() = R.layout.fragment_event
    override fun setFragment() = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dateToday = Date()
        calendar = Calendar.getInstance()
        bsdHelper = context?.let { BottomSheetDialogHelper(it, this) }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context?.let { c ->
            BirthdayAdapter(birthdays, birthdaysFull, c, dateToday, this).let {
                adapterEvent = it
                fragment_event_list.setRecyclerViewAdapter(it)
            }
        }
        viewModel = configureViewModel()
        viewModel?.let { getBirthdaysFromRoom(it) }
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

    internal fun sortList() {
        birthdays.run {
            clear()
            addAll(birthdaysFull)
            sortListWith()
            adapterEvent?.notifyDataSetChanged()
        }
    }

    private fun ArrayList<Birthday>.sortListWith() {
        sortWith(Comparator { event1, event2 ->
            (UtilsDate.getRemainingDays(
                    dateToday,
                    event1.dateBirth
            ) - UtilsDate.getRemainingDays(dateToday, event2.dateBirth))
        })
    }

    fun updateEventList(birthday: Birthday) {
        viewModel?.let {
            it.createBirthday(birthday)
            birthdaysFull.add(birthday)
            sortList()
        }
    }

    override fun onClick(o: Birthday) {
        /**Nothing**/
    }

    override fun onLongClick(o: Birthday) {
        context?.let {
            viewModel?.let {
                it.getBirthday(o.id)?.observe(this, Observer { birthday ->
                    birthday?.let { b ->
                        bsdHelper?.itemSelected(b, it)
                    }
                })
            }
        }
    }

    fun onClickDatePicker(editText: EditText, context: Context) {
        editText.text = UtilsDate.getDateInString(calendar.time).toEditable()
        editText.setOnClickListener {
            DatePickerDialog(
                    context, datePickerDialog, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    fun getDatePicker(editText: EditText, dateBirth: Date?): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            dateBirth?.let { calendar.time = it } ?: run {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            editText.text = UtilsDate.getDateInString(calendar.time).toEditable()
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun searchEvent(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterEvent?.filter?.filter(newText)
                return true
            }

        })
    }

    override fun onRequest() {
    }

    override fun onSearch(searchView: SearchView) {
        searchEvent(searchView)
    }

    override fun onAdd() {
        bsdHelper?.choiceEvents(viewModel)
    }

}

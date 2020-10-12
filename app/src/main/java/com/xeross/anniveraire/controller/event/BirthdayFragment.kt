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
import com.xeross.anniveraire.controller.base.BaseFragment
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Birthday
import com.xeross.anniveraire.model.BirthdayState
import com.xeross.anniveraire.utils.UtilsDate
import kotlinx.android.synthetic.main.bsd_birthday.view.*
import kotlinx.android.synthetic.main.bsd_choice_type_event.view.*
import kotlinx.android.synthetic.main.bsd_confirm.view.*
import kotlinx.android.synthetic.main.bsd_event_and_other.view.*
import kotlinx.android.synthetic.main.bsd_item_selected.view.*
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
    private lateinit var datePickerDialog: DatePickerDialog.OnDateSetListener

    override fun getFragmentId() = R.layout.fragment_event
    override fun setFragment() = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dateToday = Date()
        calendar = Calendar.getInstance()
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
                birthdaysFull.clear()
                birthdays.clear()
                birthdays.addAll(list)
                birthdays.sortListWith()
                birthdaysFull.addAll(birthdays)
                adapterEvent?.notifyDataSetChanged()
            }
        })
    }

    private fun ArrayList<Birthday>.sortListWith() {
        sortWith(Comparator { event1, event2 ->
            (UtilsDate.getRemainingDays(
                    dateToday,
                    event1.dateBirth
            ) - UtilsDate.getRemainingDays(dateToday, event2.dateBirth))
        })
    }

    override fun onClick(o: Birthday) {
        /**Nothing**/
    }

    override fun onLongClick(o: Birthday) {
        context?.let {
            viewModel?.getBirthday(o.id)?.observe(this, Observer { birthday ->
                birthday?.let { b ->
                    itemSelected(b)
                }
            })
        }
    }

    private fun onClickDatePicker(editText: EditText, context: Context) {
        editText.text = UtilsDate.getDateInString(calendar.time).toEditable()
        editText.setOnClickListener {
            DatePickerDialog(
                    context, datePickerDialog, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun getDatePicker(editText: EditText, dateBirth: Date?): DatePickerDialog.OnDateSetListener {
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

    override fun onSearch(searchView: SearchView) {
        searchEvent(searchView)
    }

    override fun onAdd() {
        choiceEvents()
    }

    private fun choiceEvents() {
        layoutInflater.inflate(R.layout.bsd_choice_type_event, null).let { v ->

            val bottomSheetDialog = createBSD(v) ?: return
            val context = context ?: return
            v.bsd_choice_type_event_button_birthday.setOnClickListener {
                birthday(null, context)
                bottomSheetDialog.dismiss()
            }
            v.bsd_choice_type_event_button_birthday_event.setOnClickListener {
                birthdayEventOrOther(null, false, context)
                bottomSheetDialog.dismiss()
            }
            v.bsd_choice_type_event_button_other_event.setOnClickListener {
                birthdayEventOrOther(null, true, context)
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun birthday(birthday: Birthday?, context: Context) {
        LayoutInflater.from(context).inflate(R.layout.bsd_birthday, null).let { view ->

            birthday?.let {
                datePickerDialog = getDatePicker(view.bsd_birthday_edittext_date, it.dateBirth)
                onClickDatePicker(view.bsd_birthday_edittext_date, context)
                view.bsd_birthday_edittext_lastname.text = it.lastName.toEditable()
                view.bsd_birthday_edittext_name.text = it.firstName.toEditable()
            } ?: run {
                datePickerDialog = getDatePicker(view.bsd_birthday_edittext_date, null)
                onClickDatePicker(view.bsd_birthday_edittext_date, context)
            }

            val alertDialog = createBSD(view) ?: return

            view.bsd_birthday_button_add.setOnClickListener {
                if (view.bsd_birthday_edittext_date.text!!.isEmpty() ||
                        view.bsd_birthday_edittext_lastname.text!!.isEmpty() ||
                        view.bsd_birthday_edittext_name.text!!.isEmpty()
                ) {
                    sendMissingInformationMessage()
                    return@setOnClickListener
                }
                alertDialog.dismiss()
                birthday?.let {
                    it.apply {
                        firstName = view.bsd_birthday_edittext_name.text!!.toString()
                        lastName = view.bsd_birthday_edittext_lastname.text!!.toString()
                        dateBirth = UtilsDate.getStringInDate(view.bsd_birthday_edittext_date.text!!.toString())
                    }
                    viewModel?.let { vm ->
                        vm.updateBirthday(it)
                        getBirthdaysFromRoom(vm)
                    }
                } ?: run {
                    val event = Birthday(firstName = view.bsd_birthday_edittext_name.text!!.toString(),
                            lastName = view.bsd_birthday_edittext_lastname.text!!.toString(),
                            dateBirth = UtilsDate.getStringInDate(view.bsd_birthday_edittext_date.text!!.toString()))
                    viewModel?.let { vm ->
                        vm.createBirthday(event)
                        getBirthdaysFromRoom(vm)
                    }
                }
            }
        }
    }

    private fun birthdayEventOrOther(birthday: Birthday?, isOther: Boolean, context: Context) {
        layoutInflater.inflate(R.layout.bsd_event_and_other, null).let { view ->

            birthday?.let {
                datePickerDialog = getDatePicker(view.bsd_event_other_edittext_date,
                        it.dateBirth)
                onClickDatePicker(view.bsd_event_other_edittext_date, context)
                view.bsd_event_other_edittext_name.text = it.firstName.toEditable()
            } ?: run {
                datePickerDialog = getDatePicker(view.bsd_event_other_edittext_date,
                        null)
                onClickDatePicker(view.bsd_event_other_edittext_date, context)
            }

            val alertDialog = createBSD(view) ?: return

            view.bsd_event_other_button_add.setOnClickListener {
                if (view.bsd_event_other_edittext_date.text!!.isEmpty() ||
                        view.bsd_event_other_edittext_name.text!!.isEmpty()) {
                    sendMissingInformationMessage()
                    return@setOnClickListener
                }
                alertDialog.dismiss()
                birthday?.let {
                    it.apply {
                        firstName = view.bsd_event_other_edittext_name.text!!.toString()
                        state = if (!isOther) BirthdayState.EVENT_BIRTHDAY else BirthdayState.OTHER
                        dateBirth = UtilsDate.getStringInDate(view.bsd_event_other_edittext_date.text!!.toString())
                    }
                    viewModel?.let { vm ->
                        vm.updateBirthday(it)
                        getBirthdaysFromRoom(vm)
                    }
                } ?: run {
                    val event = if (!isOther) {
                        Birthday(firstName = view.bsd_event_other_edittext_name.text!!.toString(),
                                state = BirthdayState.EVENT_BIRTHDAY,
                                dateBirth = UtilsDate.getStringInDate(view.bsd_event_other_edittext_date.text!!.toString()))
                    } else {
                        Birthday(firstName = view.bsd_event_other_edittext_name.text!!.toString(),
                                state = BirthdayState.OTHER,
                                dateBirth = UtilsDate.getStringInDate(view.bsd_event_other_edittext_date.text!!.toString()))
                    }
                    viewModel?.let { vm ->
                        vm.createBirthday(event)
                        getBirthdaysFromRoom(vm)
                    }
                }
            }
        }
    }

    private fun itemSelected(birthday: Birthday) {
        layoutInflater.inflate(R.layout.bsd_item_selected, null).let { v ->

            val bottomSheetDialog = createBSD(v) ?: return

            val context = context ?: return
            v.bsd_item_selected_edit.setOnClickListener {
                when (birthday.state) {
                    BirthdayState.BIRTHDAY -> birthday(birthday, context)
                    BirthdayState.EVENT_BIRTHDAY -> birthdayEventOrOther(birthday, false, context)
                    BirthdayState.OTHER -> birthdayEventOrOther(birthday, true, context)
                }
                bottomSheetDialog.dismiss()
            }
            v.bsd_item_selected_delete.setOnClickListener {
                confirm(birthday)
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun confirm(birthday: Birthday) {
        layoutInflater.inflate(R.layout.bsd_confirm, null).let { v ->

            val bottomSheetDialog = createBSD(v) ?: return

            v.bsd_confirm_yes.setOnClickListener {
                bottomSheetDialog.dismiss()
                viewModel?.let { vm ->
                    vm.deleteBirthday(birthday.id)
                    getBirthdaysFromRoom(vm)
                }
            }
            v.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog.dismiss()
                itemSelected(birthday)
            }
        }

    }
}

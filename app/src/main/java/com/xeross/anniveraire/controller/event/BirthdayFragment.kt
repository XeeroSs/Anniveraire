package com.xeross.anniveraire.controller.event

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
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


class BirthdayFragment : BaseFragment(), ClickListener<Birthday>, BirthdayContract.View {

    private var adapterEvent: BirthdayAdapter? = null
    private val birthdays = ArrayList<Birthday>()
    private val birthdaysFull = ArrayList<Birthday>()
    private var presenter: BirthdayPresenter? = null
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
        presenter = context?.let { BirthdayPresenter(it, this) }
        getBirthdaysFromRoom()
    }

    // get all events from room
    private fun getBirthdaysFromRoom() {
        presenter?.getBirthdays()
    }

    override fun getBirthdays() {
        presenter?.getBirthdays()
    }

    // Sort by date
    private fun ArrayList<Birthday>.sortListWith() {
        sortWith(Comparator { event1, event2 ->
            (UtilsDate.getRemainingDays(dateToday, event1.dateBirth
            ) - UtilsDate.getRemainingDays(dateToday, event2.dateBirth))
        })
    }

    // Long click event item
    override fun onClick(o: Birthday) {
        /**Nothing**/
    }

    // Long click event item
    override fun onLongClick(o: Birthday) {
        itemSelected(o)
    }

    // Click on editText for date
    private fun onClickDatePicker(editText: EditText, context: Context) {
        editText.text = UtilsDate.getDateInString(calendar.time).toEditable()
        editText.setOnClickListener {
            DatePickerDialog(
                    context, datePickerDialog, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    // Get date picker dialog
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

    // String in Editable
    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    // Search event
    private fun searchEvent(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterEvent?.filter?.filter(newText)
                return true
            }
        })
    }

    // Search event
    override fun onSearch(searchView: SearchView) {
        searchEvent(searchView)
    }

    // Add event
    override fun onAdd() {
        choiceEvents()
    }

    // Bottom sheet dialog -> choice event
    @SuppressLint("InflateParams")
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

    // Bottom sheet dialog -> Update/Create Birthday
    @SuppressLint("InflateParams")
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
                        val date = UtilsDate.getStringInDate(view.bsd_birthday_edittext_date.text!!.toString())
                                ?: return@setOnClickListener
                        dateBirth = date
                    }
                    presenter?.updateBirthday(it)
                } ?: run {
                    val date = UtilsDate.getStringInDate(view.bsd_birthday_edittext_date.text!!.toString())
                            ?: return@setOnClickListener
                    val event = Birthday(firstName = view.bsd_birthday_edittext_name.text!!.toString(),
                            lastName = view.bsd_birthday_edittext_lastname.text!!.toString(),
                            dateBirth = date)
                    presenter?.addBirthday(event)
                }
            }
        }
    }

    // Bottom sheet dialog -> Update/Create Birthday event or Other
    @SuppressLint("InflateParams")
    private fun birthdayEventOrOther(birthday: Birthday?, isOther: Boolean, context: Context) {
        layoutInflater.inflate(R.layout.bsd_event_and_other, null).let { view ->

            birthday?.let {
                datePickerDialog = getDatePicker(view.bsd_event_other_edittext_date, it.dateBirth)
                onClickDatePicker(view.bsd_event_other_edittext_date, context)
                view.bsd_event_other_edittext_name.text = it.firstName.toEditable()
            } ?: run {
                datePickerDialog = getDatePicker(view.bsd_event_other_edittext_date, null)
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
                        val date = UtilsDate.getStringInDate(view.bsd_event_other_edittext_date.text!!.toString())
                                ?: return@setOnClickListener
                        dateBirth = date
                    }
                    presenter?.updateBirthday(it)
                } ?: run {
                    val event = if (!isOther) {
                        val date = UtilsDate.getStringInDate(view.bsd_event_other_edittext_date.text!!.toString())
                                ?: return@setOnClickListener
                        Birthday(firstName = view.bsd_event_other_edittext_name.text!!.toString(),
                                state = BirthdayState.EVENT_BIRTHDAY,
                                dateBirth = date)
                    } else {
                        val date = UtilsDate.getStringInDate(view.bsd_event_other_edittext_date.text!!.toString())
                                ?: return@setOnClickListener
                        Birthday(firstName = view.bsd_event_other_edittext_name.text!!.toString(),
                                state = BirthdayState.OTHER,
                                dateBirth = date)
                    }
                    presenter?.addBirthday(event)
                }
            }
        }
    }

    // Bottom sheet dialog -> item selected
    @SuppressLint("InflateParams")
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

    // Bottom sheet dialog -> confirm delete birthday
    @SuppressLint("InflateParams")
    private fun confirm(birthday: Birthday) {
        layoutInflater.inflate(R.layout.bsd_confirm, null).let { v ->

            val bottomSheetDialog = createBSD(v) ?: return

            v.bsd_confirm_yes.setOnClickListener {
                bottomSheetDialog.dismiss()
                presenter?.let { vm ->
                    vm.deleteBirthday(birthday.id)
                    getBirthdaysFromRoom()
                }
            }
            v.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog.dismiss()
                itemSelected(birthday)
            }
        }

    }

    override fun removeBirthdays() {
        adapterEvent?.notifyDataSetChanged()
    }

    override fun getBirthdays(tObjects: List<Birthday>) {
        birthdaysFull.clear()
        birthdays.clear()
        birthdays.addAll(tObjects)
        birthdays.sortListWith()
        birthdaysFull.addAll(birthdays)
        adapterEvent?.notifyDataSetChanged()
    }
}

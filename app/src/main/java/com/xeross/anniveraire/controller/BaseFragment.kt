package com.xeross.anniveraire.controller

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.xeross.anniveraire.R
import com.xeross.anniveraire.UtilsDate.getDateInString
import com.xeross.anniveraire.UtilsDate.getStringInDate
import com.xeross.anniveraire.controller.date.DateFragment
import com.xeross.anniveraire.controller.event.EventFragment
import com.xeross.anniveraire.controller.social.SocialFragment
import com.xeross.anniveraire.model.Event
import com.xeross.anniveraire.model.EventState
import com.xeross.anniveraire.model.SortState
import kotlinx.android.synthetic.main.alertdialog_birthday.view.*
import kotlinx.android.synthetic.main.alertdialog_choice_type_event.view.*
import kotlinx.android.synthetic.main.alertdialog_event_and_other.view.*
import kotlinx.android.synthetic.main.alertdialog_sort.view.*
import java.util.*

abstract class BaseFragment : Fragment() {

    private lateinit var dateToday: Date
    private var fragment: BaseFragment? = null
    private lateinit var main: MainActivity
    private lateinit var calendar: Calendar
    private lateinit var datePickerDialog: DatePickerDialog.OnDateSetListener

    abstract fun getFragmentId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(getFragmentId(), container, false)
        dateToday = Date()
        (activity as MainActivity).let {
            main = it
            it.setBaseFragment(this)
        }
        calendar = Calendar.getInstance()
        /*  eventViewModel =
                  ViewModelProviders.of(this).get(EventViewModel::class.java)
          val textView: TextView = root.findViewById(R.id.text_home)
          eventViewModel.text.observe(viewLifecycleOwner, Observer {
              textView.text = it
          })*/
        return root
    }

    protected fun getDateToday() = dateToday

    protected fun setFragment(fragment: BaseFragment) {
        this.fragment = fragment
    }

    internal fun createPopupForChoiceEvent(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.alertdialog_choice_type_event, null)
                .let { viewDialog ->
                    val alertDialog = createDialog(context, viewDialog, "Event type")

                    viewDialog.alertdialog_choice_type_event_button_birthday.setOnClickListener {
                        createPopupForBirthday(context)
                        alertDialog.dismiss()
                    }
                    viewDialog.alertdialog_choice_type_event_button_birthday_event.setOnClickListener {
                        createPopupForBirthdayEventOrOther(context, false)
                        alertDialog.dismiss()
                    }
                    viewDialog.alertdialog_choice_type_event_button_other_event.setOnClickListener {
                        createPopupForBirthdayEventOrOther(context, true)
                        alertDialog.dismiss()
                    }
                }
    }

    private fun createPopupForBirthday(context: Context) {
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
                        dateBirth = getStringInDate(view.alertdialog_birthday_edittext_date.text!!.toString()))

                getEventFragment()?.apply {
                    updateEventList(event)
                }
                alertDialog.dismiss()
            }
        }
    }

    private fun createPopupForBirthdayEventOrOther(context: Context, isOther: Boolean) {
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
                            dateBirth = getStringInDate(view.alertdialog_event_and_other_edittext_date.text!!.toString()))
                } else {
                    Event(firstName = view.alertdialog_event_and_other_name.text!!.toString(),
                            state = EventState.OTHER,
                            dateBirth = getStringInDate(view.alertdialog_event_and_other_edittext_date.text!!.toString()))
                }

                getEventFragment()?.apply {
                    updateEventList(event)
                }
                alertDialog.dismiss()
            }
        }
    }

    private fun onClickDatePicker(editText: EditText, context: Context) {
        editText.text = getDateInString(calendar.time).toEditable()
        editText.setOnClickListener {
            DatePickerDialog(context, datePickerDialog, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun getDatePicker(editText: EditText): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            editText.text = getDateInString(calendar.time).toEditable()
        }
    }

    private fun getSocialFragment() = getFragment<SocialFragment>()
    private fun getDateFragment() = getFragment<DateFragment>()
    private fun getEventFragment() = getFragment<EventFragment>()

    private inline fun <reified T> getFragment() =
            fragment?.takeIf { fragment is T }?.let {
                it as T
            } ?: run {
                main.sendErrorMessage()
                null
            }

    private fun createDialog(context: Context, view: View?, title: String) = AlertDialog.Builder(context).apply {
        setView(view)
        setTitle(title)
    }.show()

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    internal fun searchEvent() {
        TODO("Not yet implemented")
    }

    internal fun sortEvents(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.alertdialog_sort, null).let { viewDialog ->
            val alertDialog = createDialog(context, viewDialog, "Sort")
            viewDialog.alertdialog_sort_name.setOnClickListener {
                onClickChoiceSort(alertDialog, SortState.NAME)
            }
            viewDialog.alertdialog_sort_age_descending.setOnClickListener {
                onClickChoiceSort(alertDialog, SortState.AGE_DESCENDING)
            }
            viewDialog.alertdialog_sort_remaining_days.setOnClickListener {
                onClickChoiceSort(alertDialog, SortState.DAY_REMAINING)
            }
        }
    }

    private fun onClickChoiceSort(alertDialog: AlertDialog, sortState: SortState) {
        getEventFragment()?.apply {
            setSortBy(sortState)
            sortList()
        }
        alertDialog.dismiss()
    }
}
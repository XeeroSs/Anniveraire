package com.xeross.anniveraire.controller

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.date.DateFragment
import com.xeross.anniveraire.controller.event.EventFragment
import com.xeross.anniveraire.controller.social.SocialFragment
import com.xeross.anniveraire.model.Birthday
import com.xeross.anniveraire.model.BirthdayState
import com.xeross.anniveraire.model.SortState
import com.xeross.anniveraire.utils.UtilsDate.getDateInString
import kotlinx.android.synthetic.main.bsd_choice_sort.view.*
import kotlinx.android.synthetic.main.bsd_choice_type_event.view.*
import kotlinx.android.synthetic.main.bsd_confirm.view.*
import kotlinx.android.synthetic.main.bsd_item_selected.view.*
import java.util.*

abstract class BaseFragment : Fragment() {

    private lateinit var dateToday: Date
    private var fragment: BaseFragment? = null
    protected lateinit var main: MainActivity
    private lateinit var calendar: Calendar
    protected lateinit var datePickerDialog: DatePickerDialog.OnDateSetListener

    abstract fun getFragmentId(): Int

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    protected fun getDateToday() = dateToday

    protected fun setFragment(fragment: BaseFragment) {
        this.fragment = fragment
    }

    protected fun onClickDatePicker(editText: EditText, context: Context) {
        editText.text = getDateInString(calendar.time).toEditable()
        editText.setOnClickListener {
            DatePickerDialog(
                    context, datePickerDialog, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    protected fun getDatePicker(editText: EditText): DatePickerDialog.OnDateSetListener {
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

    private inline fun <reified T : BaseFragment> getFragment() = (fragment as? T) ?: run {
        main.sendErrorMessage()
        null
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    internal fun searchEvent(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                getEventFragment()?.getAdapter()?.filter?.filter(newText)
                return true
            }

        })
    }

    internal fun sortEvents(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.bsd_choice_sort, null).let { viewDialog ->
            val bottomSheetDialog = createBSD(context, viewDialog)
            onClickChoiceSort(viewDialog.bsd_sort_name, bottomSheetDialog, SortState.NAME)
            onClickChoiceSort(viewDialog.bsd_sort_age_descending, bottomSheetDialog, SortState.AGE_DESCENDING)
            onClickChoiceSort(viewDialog.bsd_sort_remaining_days, bottomSheetDialog, SortState.DAY_REMAINING)
        }
    }

    private fun onClickChoiceSort(view: Button, bottomSheetDialog: BottomSheetDialog, sortState: SortState) {
        view.setOnClickListener {
            getEventFragment()?.onClickChoiceSort(bottomSheetDialog, sortState)
        }
    }

    internal fun createBSDChoiceEvents(context: Context) {
        layoutInflater.inflate(R.layout.bsd_choice_type_event, null).let {

            val bottomSheetDialog = createBSD(context, it)

            it.bsd_choice_type_event_button_birthday.setOnClickListener {
                getEventFragment()?.createBSDBirthday(context)
                bottomSheetDialog.dismiss()
            }
            it.bsd_choice_type_event_button_birthday_event.setOnClickListener {
                getEventFragment()?.createPopupForBirthdayEventOrOther(context, false)
                bottomSheetDialog.dismiss()
            }
            it.bsd_choice_type_event_button_other_event.setOnClickListener {
                getEventFragment()?.createPopupForBirthdayEventOrOther(context, true)
                bottomSheetDialog.dismiss()
            }
        }

    }

    internal fun createBSDItemSelected(context: Context, birthday: Birthday) {
        layoutInflater.inflate(R.layout.bsd_item_selected, null).run {

            val bottomSheetDialog = createBSD(context, this)

            bsd_item_selected_edit.setOnClickListener {
                when (getFragmentId()) {
                    R.layout.fragment_event -> {
                        getEventFragment()?.let {
                            when (birthday.state) {
                                BirthdayState.BIRTHDAY -> it.createBSDBirthday(context)
                                BirthdayState.EVENT_BIRTHDAY -> it.createPopupForBirthdayEventOrOther(context, false)
                                BirthdayState.OTHER -> it.createPopupForBirthdayEventOrOther(context, true)
                            }
                        }
                    }
                }
                bottomSheetDialog.dismiss()
            }
            bsd_item_selected_delete.setOnClickListener {
                createBSDConfirm(context, birthday)
                bottomSheetDialog.dismiss()
            }
        }

    }

    internal fun createBSDConfirm(context: Context, birthday: Birthday) {
        layoutInflater.inflate(R.layout.bsd_confirm, null).let {

            val bottomSheetDialog = createBSD(context, it)

            it.bsd_confirm_yes.setOnClickListener {
                // do stuff (room) ..
                bottomSheetDialog.dismiss()
                getEventFragment()?.getList()?.remove(birthday)
                getEventFragment()?.getAdapter()?.notifyDataSetChanged()
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog.dismiss()
                createBSDItemSelected(context, birthday)
            }
        }

    }

    private fun createBSD(context: Context, view: View) =
            BottomSheetDialog(context).apply {
                setContentView(view)
                show()
            }
}
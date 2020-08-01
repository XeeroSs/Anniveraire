package com.xeross.anniveraire.utils

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.BaseEventFragment
import com.xeross.anniveraire.controller.event.BirthdayFragment
import com.xeross.anniveraire.model.Birthday
import com.xeross.anniveraire.model.BirthdayState
import com.xeross.anniveraire.model.SortState
import kotlinx.android.synthetic.main.bsd_birthday.view.*
import kotlinx.android.synthetic.main.bsd_choice_sort.view.*
import kotlinx.android.synthetic.main.bsd_choice_type_event.view.*
import kotlinx.android.synthetic.main.bsd_confirm.view.*
import kotlinx.android.synthetic.main.bsd_event_and_other.view.*
import kotlinx.android.synthetic.main.bsd_item_selected.view.*

class BottomSheetDialogHelper(private val context: Context, private val fragment: BaseEventFragment) {

    // ---------- BottomSheetDialog ----------

    fun choiceEvents() {
        fragment.layoutInflater.inflate(R.layout.bsd_choice_type_event, null).let {

            val bottomSheetDialog = createBSD(it)

            it.bsd_choice_type_event_button_birthday.setOnClickListener {
                if (fragment is BirthdayFragment) birthday(null)
                bottomSheetDialog.dismiss()
            }
            it.bsd_choice_type_event_button_birthday_event.setOnClickListener {
                if (fragment is BirthdayFragment) birthdayEventOrOther(null, false)
                bottomSheetDialog.dismiss()
            }
            it.bsd_choice_type_event_button_other_event.setOnClickListener {
                if (fragment is BirthdayFragment) birthdayEventOrOther(null, true)
                bottomSheetDialog.dismiss()
            }
        }

    }

    fun sortEvents() {
        fragment.layoutInflater.inflate(R.layout.bsd_choice_sort, null).let {
            val bottomSheetDialog = createBSD(it)
            onClickChoiceSort(it.bsd_sort_name, bottomSheetDialog, SortState.NAME)
            onClickChoiceSort(it.bsd_sort_age_descending, bottomSheetDialog, SortState.AGE_DESCENDING)
            onClickChoiceSort(it.bsd_sort_remaining_days, bottomSheetDialog, SortState.DAY_REMAINING)
        }
    }

    private fun birthday(birthday: Birthday?) {
        @Suppress("SimpleRedundantLet")
        val bFragment = fragment.getEventFragment()?.let { it } ?: return

        LayoutInflater.from(context).inflate(R.layout.bsd_birthday, null).let { view ->

            birthday?.let {
                bFragment.datePickerDialog = bFragment.getDatePicker(view.bsd_birthday_edittext_date, it.dateBirth)
                bFragment.onClickDatePicker(view.bsd_birthday_edittext_date, context)
                view.bsd_birthday_edittext_lastname.text = it.lastName.toEditable()
                view.bsd_birthday_edittext_name.text = it.firstName.toEditable()
            } ?: run {
                bFragment.datePickerDialog = bFragment.getDatePicker(view.bsd_birthday_edittext_date, null)
                bFragment.onClickDatePicker(view.bsd_birthday_edittext_date, context)
            }

            val alertDialog = createBSD(view)

            view.bsd_birthday_button_add.setOnClickListener {
                if (view.bsd_birthday_edittext_date.text!!.isEmpty() ||
                        view.bsd_birthday_edittext_lastname.text!!.isEmpty() ||
                        view.bsd_birthday_edittext_name.text!!.isEmpty()
                ) {
                    bFragment.main.sendMissingInformationMessage()
                    return@setOnClickListener
                }
                birthday?.let {
                    it.apply {
                        firstName = view.bsd_birthday_edittext_name.text!!.toString()
                        lastName = view.bsd_birthday_edittext_lastname.text!!.toString()
                        dateBirth = UtilsDate.getStringInDate(view.bsd_birthday_edittext_date.text!!.toString())
                    }
                    bFragment.sortList()
                } ?: run {
                    val event = Birthday(
                            firstName = view.bsd_birthday_edittext_name.text!!.toString(),
                            lastName = view.bsd_birthday_edittext_lastname.text!!.toString(),
                            dateBirth = UtilsDate.getStringInDate(view.bsd_birthday_edittext_date.text!!.toString())
                    )

                    bFragment.updateEventList(event)
                }

                alertDialog.dismiss()
            }
        }
    }

    private fun birthdayEventOrOther(birthday: Birthday?, isOther: Boolean) {
        @Suppress("SimpleRedundantLet")
        val bFragment = fragment.getEventFragment()?.let { it } ?: return

        fragment.layoutInflater.inflate(R.layout.bsd_event_and_other, null).let { view ->

            birthday?.let {
                bFragment.datePickerDialog = bFragment.getDatePicker(view.bsd_event_other_edittext_date, it.dateBirth)
                bFragment.onClickDatePicker(view.bsd_event_other_edittext_date, context)
                view.bsd_event_other_edittext_name.text = it.firstName.toEditable()
            } ?: run {
                bFragment.datePickerDialog = bFragment.getDatePicker(view.bsd_event_other_edittext_date, null)
                bFragment.onClickDatePicker(view.bsd_event_other_edittext_date, context)
            }

            val alertDialog = createBSD(view)

            view.bsd_event_other_button_add.setOnClickListener {
                if (view.bsd_event_other_edittext_date.text!!.isEmpty() ||
                        view.bsd_event_other_edittext_name.text!!.isEmpty()
                ) {
                    bFragment.main.sendMissingInformationMessage()
                    return@setOnClickListener
                }

                birthday?.let {
                    it.apply {
                        firstName = view.bsd_event_other_edittext_name.text!!.toString()
                        state = if (!isOther) BirthdayState.EVENT_BIRTHDAY else BirthdayState.OTHER
                        dateBirth = UtilsDate.getStringInDate(view.bsd_event_other_edittext_date.text!!.toString())
                    }
                    bFragment.sortList()
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

                    bFragment.updateEventList(event)
                }
                alertDialog.dismiss()
            }
        }
    }

    fun itemSelected(birthday: Birthday) {
        fragment.layoutInflater.inflate(R.layout.bsd_item_selected, null).run {

            val bottomSheetDialog = createBSD(this)

            bsd_item_selected_edit.setOnClickListener {
                when (fragment.getFragmentId()) {
                    R.layout.fragment_event -> {
                        fragment.getEventFragment()?.let {
                            when (birthday.state) {
                                BirthdayState.BIRTHDAY -> birthday(birthday)
                                BirthdayState.EVENT_BIRTHDAY -> birthdayEventOrOther(birthday, false)
                                BirthdayState.OTHER -> birthdayEventOrOther(birthday, true)
                            }
                        }
                    }
                }
                bottomSheetDialog.dismiss()
            }
            bsd_item_selected_delete.setOnClickListener {
                confirm(birthday)
                bottomSheetDialog.dismiss()
            }
        }

    }

    private fun confirm(birthday: Birthday) {
        fragment.layoutInflater.inflate(R.layout.bsd_confirm, null).let {

            val bottomSheetDialog = createBSD(it)

            it.bsd_confirm_yes.setOnClickListener {
                // do stuff (room) ..
                bottomSheetDialog.dismiss()
                fragment.getEventFragment()?.getList()?.remove(birthday)
                fragment.getEventFragment()?.getAdapter()?.notifyDataSetChanged()
            }
            it.bsd_confirm_no.setOnClickListener {
                bottomSheetDialog.dismiss()
                itemSelected(birthday)
            }
        }

    }

    // ---------- Other ----------

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun onClickChoiceSort(view: Button, bottomSheetDialog: BottomSheetDialog, sortState: SortState) {
        view.setOnClickListener {
            fragment.getEventFragment()?.onClickChoiceSort(bottomSheetDialog, sortState)
        }
    }

    private fun createBSD(view: View) = BottomSheetDialog(context).apply {
        setContentView(view)
        show()
    }
}
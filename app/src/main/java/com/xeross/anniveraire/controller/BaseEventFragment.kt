package com.xeross.anniveraire.controller

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import com.xeross.anniveraire.utils.AlarmReceiver
import com.xeross.anniveraire.utils.BottomSheetDialogHelper
import com.xeross.anniveraire.utils.UtilsDate
import java.util.*

abstract class BaseEventFragment : BaseFragment() {

    private lateinit var calendar: Calendar
    private lateinit var dateToday: Date
    private var bsdHelper: BottomSheetDialogHelper? = null
    lateinit var datePickerDialog: DatePickerDialog.OnDateSetListener
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    private fun initializeAlarmManager() {
        alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        alarmMgr?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60 * 1000,
                alarmIntent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dateToday = Date()
        calendar = Calendar.getInstance()
        bsdHelper = context?.let { BottomSheetDialogHelper(it, this) }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun getBSDHelper() = bsdHelper

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

    protected fun getDateToday() = dateToday

    fun onClickDatePicker(editText: EditText, context: Context) {
        editText.text = UtilsDate.getDateInString(calendar.time).toEditable()
        editText.setOnClickListener {
            DatePickerDialog(
                    context, datePickerDialog, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    fun getDatePicker(editText: EditText): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            editText.text = UtilsDate.getDateInString(calendar.time).toEditable()
        }
    }
}
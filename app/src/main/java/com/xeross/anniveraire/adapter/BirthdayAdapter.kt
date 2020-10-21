package com.xeross.anniveraire.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.xeross.anniveraire.R
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Birthday
import com.xeross.anniveraire.model.BirthdayState
import com.xeross.anniveraire.utils.UtilsDate
import kotlinx.android.synthetic.main.birthday_cell.view.*
import java.util.*

class BirthdayAdapter(objectList: ArrayList<Birthday>, objectListFull: ArrayList<Birthday>,
                      context: Context, private val dateToday: Date,
                      clickListener: ClickListener<Birthday>) :
        BaseAdapter<BirthdayAdapter.ViewHolder, Birthday>(objectList, objectListFull, context, clickListener) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameEvent: TextView = itemView.birthday_cell_name
        val dateEvent: TextView = itemView.birthday_cell_date
        val ageEvent: TextView = itemView.birthday_cell_age
        val cardView: CardView = itemView.birthday_cell_item
        val remainingDaysEvent: TextView = itemView.birthday_cell_remaining_days
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.birthday_cell, parent, false))

    override fun filterItem(dObject: Birthday, filterPattern: String) =
            (dObject.firstName.containsString(filterPattern) ||
                    dObject.lastName.containsString(filterPattern)
                    || dObject.dateBirth.toString().containsString(filterPattern))

    override fun updateItem(holder: ViewHolder, dObject: Birthday) {
        setName(holder, dObject)
        setRemainingDays(holder, dObject.dateBirth)
        when (dObject.state) {
            BirthdayState.BIRTHDAY -> setBirthdayItem(holder, dObject)
            BirthdayState.EVENT_BIRTHDAY -> setEventBirthdayItem(holder, dObject)
            BirthdayState.OTHER -> setOtherItem(holder, dObject)
        }
    }

    private fun setOtherItem(holder: ViewHolder, dObject: Birthday) {
        holder.dateEvent.text = UtilsDate.getDateWithoutYearInString(dObject.dateBirth, context)
        holder.ageEvent.visibility = View.GONE
    }

    @Suppress("DEPRECATION")
    private fun setEventBirthdayItem(holder: ViewHolder, dObject: Birthday) {
        eventBirthday(holder, dObject)
        holder.remainingDaysEvent.setTextColor(context.resources.getColor(R.color.colorPrimary))
    }

    private fun setBirthdayItem(holder: ViewHolder, dObject: Birthday) {
        eventBirthday(holder, dObject)
    }

    private fun setName(holder: ViewHolder, dObject: Birthday) {
        holder.nameEvent.text = if (dObject.lastName == "") dObject.firstName else context.getString(R.string.firstname_lastname_event, dObject.firstName, dObject.lastName)
    }

    private fun setRemainingDays(holder: ViewHolder, date: Date) {
        holder.remainingDaysEvent.text = context.getString(R.string.remaining_days, UtilsDate.getRemainingDays(dateToday, date))
    }

    private fun eventBirthday(holder: ViewHolder, birthday: Birthday) {
        holder.dateEvent.text = UtilsDate.getDateInString(birthday.dateBirth, context)
        holder.ageEvent.text = context.getString(R.string.age_event, UtilsDate.getAgeEvent(dateToday, birthday.dateBirth).plus(1))
    }

    override fun onClick(holder: ViewHolder, dObject: Birthday) {
        holder.cardView.setOnLongClickListener {
            clickListener.onLongClick(dObject)
            true
        }
    }
}
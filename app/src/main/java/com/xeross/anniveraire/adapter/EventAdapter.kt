package com.xeross.anniveraire.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xeross.anniveraire.R
import com.xeross.anniveraire.UtilsDate
import com.xeross.anniveraire.model.Event
import com.xeross.anniveraire.model.EventState
import kotlinx.android.synthetic.main.event_cell.view.*
import java.util.*

class EventAdapter(
        private val context: Context?,
        private val events: List<Event>?, private val dateToday: Date
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            EventViewHolder(LayoutInflater.from(context).inflate(R.layout.event_cell, parent, false))

    override fun getItemCount() = events?.let { return it.size } ?: 0

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        context?.let { contextMain ->
            events?.let {
                val event = it[position]
                updateItem(contextMain, event, holder)
            }
        }
    }

    private fun updateItem(contextMain: Context, event: Event, holder: EventViewHolder) {
        holder.nameEvent.text = if (event.lastName == "") event.firstName else
            contextMain.getString(R.string.firstname_lastname_event, event.firstName, event.lastName)
        holder.remainingDaysEvent.text = contextMain.getString(R.string.remaining_days, UtilsDate.getRemainingDays(event.dateBirth, dateToday))
        when (event.state) {
            EventState.BIRTHDAY -> {
                eventBirthday(holder, event, contextMain, R.drawable.ic_birthday_cake)
            }
            EventState.EVENT_BIRTHDAY -> {
                eventBirthday(holder, event, contextMain, R.drawable.im_calendar_event)
                holder.remainingDaysEvent.setTextColor(contextMain.resources.getColor(R.color.colorPrimary))
            }
            EventState.OTHER -> {
                Glide.with(contextMain).load(R.drawable.im_champagne).into(holder.imageEvent)
                holder.dateEvent.text = UtilsDate.getDateWithoutYearInString(event.dateBirth, contextMain)
                holder.ageEvent.visibility = View.GONE
            }
        }
    }

    private fun eventBirthday(holder: EventViewHolder, event: Event, contextMain: Context, imageDrawable: Int) {
        Glide.with(contextMain).load(imageDrawable).into(holder.imageEvent)
        holder.dateEvent.text = UtilsDate.getDateInString(event.dateBirth, contextMain)
        holder.ageEvent.text = contextMain.getString(R.string.age_event, UtilsDate.getAgeEvent(dateToday, event.dateBirth).plus(1))
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageEvent: ImageView = itemView.event_cell_image
        val nameEvent: TextView = itemView.event_cell_name
        val dateEvent: TextView = itemView.event_cell_date
        val ageEvent: TextView = itemView.event_cell_age
        val remainingDaysEvent: TextView = itemView.event_cell_remaining_days
    }
}

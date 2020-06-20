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
import com.xeross.anniveraire.Utils
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
        Glide.with(contextMain).load(if (event.imageURL == "") R.drawable.im_gift else event.imageURL).into(holder.imageEvent)
        holder.nameEvent.text = if (event.lastName == "") event.firstName else
            contextMain.getString(R.string.firstname_lastname_event, event.firstName, event.lastName)
        holder.remainingDaysEvent.text = contextMain.getString(R.string.remaining_days, Utils.getRemainingDays(event.dateBirth, dateToday))
        when (event.state) {
            EventState.BIRTHDAY -> holder.dateEvent.text = Utils.getDateInString(event.dateBirth, contextMain)
            EventState.EVENT_BIRTHDAY -> {
                holder.dateEvent.text = Utils.getDateInString(event.dateBirth, contextMain)
                holder.remainingDaysEvent.setTextColor(contextMain.resources.getColor(R.color.colorPrimary))
            }
            EventState.OTHER -> {
                holder.dateEvent.text = Utils.getDateWithoutYearInString(event.dateBirth, contextMain)
                holder.ageEvent.visibility = View.GONE
            }
        }
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageEvent: ImageView = itemView.event_cell_image
        val nameEvent: TextView = itemView.event_cell_name
        val dateEvent: TextView = itemView.event_cell_date
        val ageEvent: TextView = itemView.event_cell_age
        val remainingDaysEvent: TextView = itemView.event_cell_remaining_days
    }
}

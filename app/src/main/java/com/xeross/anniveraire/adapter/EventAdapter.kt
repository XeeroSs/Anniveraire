package com.xeross.anniveraire.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xeross.anniveraire.R
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Event
import com.xeross.anniveraire.utils.UtilsDate
import kotlinx.android.synthetic.main.event_cell.view.*
import java.util.*
import kotlin.collections.ArrayList

class EventAdapter(
    private val context: Context?,
    private var events: ArrayList<Event>?, private val dateToday: Date,
    private val clickListener: ClickListener<Event>
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(), Filterable {

    private var eventsFiltered = events

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EventViewHolder(LayoutInflater.from(context).inflate(R.layout.event_cell, parent, false))

    override fun getItemCount() = events?.let { return it.size } ?: 0

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        context?.let { contextMain ->
            events?.let {
                val event = it[position]
                updateItem(contextMain, event, holder)
                onClick(holder, event)
            }
        }
    }

    private fun onClick(
        holder: EventViewHolder,
        event: Event
    ) {
        holder.itemView.setOnClickListener {
            clickListener.onClick(event)
        }
        holder.itemView.setOnLongClickListener {
            clickListener.onLongClick(event)
            true
        }
    }

    fun updateList(events: ArrayList<Event>?) {
        eventsFiltered = events
    }

    private fun updateItem(contextMain: Context, event: Event, holder: EventViewHolder) {
        holder.nameEvent.text = event.name
        holder.remainingDaysEvent.text = contextMain.getString(
            R.string.remaining_days,
            UtilsDate.getRemainingDays(dateToday, event.date)
        )
        holder.dateEvent.text = UtilsDate.getDateWithHourInString(event.date, contextMain)
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameEvent: TextView = itemView.event_cell_name
        val dateEvent: TextView = itemView.event_cell_date
        val remainingDaysEvent: TextView = itemView.event_cell_remaining_days
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterPattern =
                    charSequence.toString().toLowerCase(Locale.getDefault()).trim { it <= ' ' }
                val filteredList: MutableList<Event> = ArrayList()
                if (filterPattern.isEmpty()) {
                    events?.let { filteredList.addAll(it) }
                } else {
                    eventsFiltered?.let {
                        for (event in it) {
                            if (event.name.contains(charSequence) ||
                                event.label.contains(charSequence)
                                || event.date.toString().contains(charSequence)
                            ) {
                                filteredList.add(event)
                            }
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                events?.clear()
                events?.addAll(filterResults.values as List<Event>)
                notifyDataSetChanged()
            }
        }
    }
}

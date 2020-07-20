package com.xeross.anniveraire.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.xeross.anniveraire.R
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Event
import com.xeross.anniveraire.utils.UtilsDate
import kotlinx.android.synthetic.main.event_cell.view.*
import java.util.*

class EventAdapter(objectList: ArrayList<Event>, objectListFull: ArrayList<Event>, context: Context, dateToday: Date, clickListener: ClickListener<Event>) : BaseAdapter<EventAdapter.ViewHolder, Event>(objectList, objectListFull, context, dateToday, clickListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.event_cell, parent, false))


    private fun setRemainingDays(holder: ViewHolder, date: Date) {
        holder.remainingDaysEvent.text = context.getString(
                R.string.remaining_days,
                UtilsDate.getRemainingDays(dateToday, date)
        )
    }


    override fun onClick(holder: ViewHolder, dObject: Event) {
        holder.cardView.setOnLongClickListener {
            clickListener.onLongClick(dObject)
            true
        }
        holder.cardView.setOnClickListener {
            clickListener.onClick(dObject)
        }
    }

    override fun filterItem(dObject: Event, filterPattern: String) =
            (dObject.name.containsString(filterPattern) ||
                    dObject.label.containsString(filterPattern)
                    || dObject.date.toString().containsString(filterPattern))

    override fun updateItem(holder: ViewHolder, dObject: Event) {
        holder.nameEvent.text = dObject.name
        setRemainingDays(holder, dObject.date)
        holder.dateEvent.text = UtilsDate.getDateWithoutYearInString(dObject.date, context)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageEvent: ImageView = itemView.event_cell_image
        val nameEvent: TextView = itemView.event_cell_name
        val dateEvent: TextView = itemView.event_cell_date
        val cardView: CardView = itemView.event_cell_item
        val remainingDaysEvent: TextView = itemView.event_cell_remaining_days
    }
}

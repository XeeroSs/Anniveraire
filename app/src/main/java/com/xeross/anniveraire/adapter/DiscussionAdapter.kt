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
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.utils.UtilsDate
import kotlinx.android.synthetic.main.discussion_cell.view.*
import java.util.*

class DiscussionAdapter(objectList: ArrayList<Discussion>,
                        objectListFull: ArrayList<Discussion>,
                        clickListener: ClickListener<Discussion>,
                        context: Context) :
        BaseAdapter<DiscussionAdapter.ViewHolder, Discussion>(objectList, objectListFull, context, clickListener) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameDiscussion: TextView = itemView.discussion_text
        val dateDiscussion: TextView = itemView.discussion_date
        val cardView: CardView = itemView.discussion_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.discussion_cell, parent, false))

    override fun onClick(holder: ViewHolder, dObject: Discussion) {
        holder.cardView.setOnLongClickListener {
            clickListener.onLongClick(dObject)
            true
        }

        holder.cardView.setOnClickListener {
            clickListener.onClick(dObject)
        }
    }

    override fun filterItem(dObject: Discussion, filterPattern: String) =
            (dObject.name.containsString(filterPattern) ||
                    dObject.activityDate.toString().containsString(filterPattern))

    override fun updateItem(holder: ViewHolder, dObject: Discussion) {
        holder.nameDiscussion.text = dObject.name
        holder.dateDiscussion.text = UtilsDate.getDateWithHourInString(dObject.activityDate)
    }
}
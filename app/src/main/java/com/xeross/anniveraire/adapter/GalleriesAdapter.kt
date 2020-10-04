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
import com.xeross.anniveraire.model.Gallery
import com.xeross.anniveraire.utils.UtilsDate
import kotlinx.android.synthetic.main.galleries_cell.view.*
import java.util.*

class GalleriesAdapter(private val objectList: ArrayList<Gallery>,
                       objectListFull: ArrayList<Gallery>,
                       clickListener: ClickListener<Gallery>,
                       context: Context) : BaseAdapter<GalleriesAdapter.ViewHolder, Gallery>(objectList, objectListFull, context, clickListener) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameGallery: TextView = itemView.galleries_text
        val dateGallery: TextView = itemView.galleries_date
        val cardView: CardView = itemView.galleries_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.galleries_cell, parent, false))

    override fun updateItem(holder: ViewHolder, dObject: Gallery) {
        holder.dateGallery.text = UtilsDate.getDateWithHourInString(dObject.activityDate)
        holder.nameGallery.text = dObject.name
    }

    override fun onClick(holder: ViewHolder, dObject: Gallery) {
        holder.cardView.setOnLongClickListener {
            clickListener.onLongClick(dObject)
            true
        }

        holder.cardView.setOnClickListener {
            clickListener.onClick(dObject)
        }
    }

    override fun filterItem(dObject: Gallery, filterPattern: String) =
            (dObject.name.containsString(filterPattern) ||
                    dObject.activityDate.toString().containsString(filterPattern))
}
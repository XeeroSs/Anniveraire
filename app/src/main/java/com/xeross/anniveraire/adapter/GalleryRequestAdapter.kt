package com.xeross.anniveraire.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.gallery.request.GalleryRequestActivity
import com.xeross.anniveraire.model.Gallery
import kotlinx.android.synthetic.main.disussion_request_cell.view.*
import java.util.*

class GalleryRequestAdapter(private val galleries: ArrayList<Gallery>,
                            private val context: GalleryRequestActivity) : RecyclerView.Adapter<GalleryRequestAdapter.ViewHolder>() {

    override fun getItemCount() = galleries.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageDiscussion: ImageView = itemView.discussion_request_image
        val nameDiscussion: TextView = itemView.discussion_request_text
        val buttonJoin: Button = itemView.discussion_request_join
        val buttonDeny: Button = itemView.discussion_request_deny
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.disussion_request_cell, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gallery = galleries[position]
        updateItem(holder, gallery)
        onClick(holder, gallery)
    }

    private fun onClick(holder: ViewHolder, gallery: Gallery) {
        holder.buttonJoin.setOnClickListener {
            context.joinRequest(gallery)
        }
        holder.buttonDeny.setOnClickListener {
            context.deleteRequest(gallery)
        }
    }

    private fun updateItem(holder: ViewHolder, discussion: Gallery) {
        holder.nameDiscussion.text = discussion.name
    }
}
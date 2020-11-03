package com.xeross.anniveraire.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xeross.anniveraire.R
import com.xeross.anniveraire.listener.RequestListener
import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.Gallery
import kotlinx.android.synthetic.main.disussion_request_cell.view.*

class RequestAdapter<T>(private val context: Context, private val tObjects: ArrayList<T>,
                        private val listener: RequestListener<T>) :
        RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

    override fun getItemCount() = tObjects.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageDiscussion: ImageView = itemView.discussion_request_image
        val nameDiscussion: TextView = itemView.discussion_request_text
        val buttonJoin: ImageButton = itemView.discussion_request_join
        val buttonDeny: ImageButton = itemView.discussion_request_deny
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.disussion_request_cell, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tObject = tObjects[position]
        updateItem(holder, tObject)
        onClick(holder, tObject)
    }

    private fun onClick(holder: ViewHolder, tObject: T) {
        holder.buttonJoin.setOnClickListener {
            listener.join(tObject)
        }
        holder.buttonDeny.setOnClickListener {
            listener.deny(tObject)
        }
    }

    @Suppress("DEPRECATION")
    private fun updateItem(holder: ViewHolder, tObject: T) {
        when (tObject) {
            is Gallery -> {
                holder.nameDiscussion.text = tObject.name
                Glide.with(context)
                        .load(context.resources.getDrawable(R.drawable.image_circle))
                        .into(holder.imageDiscussion)
            }
            is Discussion -> {
                holder.nameDiscussion.text = tObject.name
                Glide.with(context).load(context.resources.getDrawable(R.drawable.chat_circle))
                        .into(holder.imageDiscussion)
            }
        }
    }
}
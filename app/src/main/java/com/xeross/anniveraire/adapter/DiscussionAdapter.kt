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
import com.xeross.anniveraire.model.Discussion
import kotlinx.android.synthetic.main.discussion_cell.view.*
import java.util.*

class DiscussionAdapter(private val discussions: ArrayList<Discussion>,
        protected val context: Context,
        protected val clickListener: ClickListener<Discussion>) : RecyclerView.Adapter<DiscussionAdapter.ViewHolder>() {

    override fun getItemCount() = discussions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageDiscussion: ImageView = itemView.discussion_image
        val nameDiscussion: TextView = itemView.discussion_text
        val cardView: CardView = itemView.discussion_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.discussion_cell, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val discussion = discussions[position]
        updateItem(holder, discussion)
        onClick(holder, discussion)
    }

    private fun onClick(holder: ViewHolder, discussion: Discussion) {
        holder.cardView.setOnLongClickListener {
            clickListener.onLongClick(discussion)
            true
        }

        holder.cardView.setOnClickListener {
            clickListener.onClick(discussion)
        }
    }

    private fun updateItem(holder: ViewHolder, discussion: Discussion) {
        holder.nameDiscussion.text = discussion.name
    }
}
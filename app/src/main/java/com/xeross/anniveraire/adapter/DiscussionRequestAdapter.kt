package com.xeross.anniveraire.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.discussion.request.DiscussionRequestActivity
import com.xeross.anniveraire.model.Discussion
import kotlinx.android.synthetic.main.disussion_request_cell.view.*
import java.util.*

class DiscussionRequestAdapter(private val discussions: ArrayList<Discussion>,
                               private val context: DiscussionRequestActivity) : RecyclerView.Adapter<DiscussionRequestAdapter.ViewHolder>() {

    override fun getItemCount() = discussions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageDiscussion: ImageView = itemView.discussion_request_image
        val nameDiscussion: TextView = itemView.discussion_request_text
        val buttonJoin: Button = itemView.discussion_request_join
        val buttonDeny: Button = itemView.discussion_request_deny
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.disussion_request_cell, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val discussion = discussions[position]
        updateItem(holder, discussion)
        onClick(holder, discussion)
    }

    private fun onClick(holder: ViewHolder, discussion: Discussion) {
        holder.buttonJoin.setOnClickListener {
            context.joinRequest(discussion)
        }
        holder.buttonDeny.setOnClickListener {
            context.deleteRequest(discussion)
        }
    }

    private fun updateItem(holder: ViewHolder, discussion: Discussion) {
        holder.nameDiscussion.text = discussion.name
    }
}
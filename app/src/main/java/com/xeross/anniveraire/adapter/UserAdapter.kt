package com.xeross.anniveraire.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xeross.anniveraire.R
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.User
import kotlinx.android.synthetic.main.user_cell.view.*
import java.util.*

class UserAdapter(private val discussions: ArrayList<User>,
                  private val context: Context,
                  private val clickListener: ClickListener<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun getItemCount() = discussions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageDiscussion: ImageView = itemView.discussion_user_image
        val nameDiscussion: TextView = itemView.discussion_user_text
        val cardView: CardView = itemView.discussion_user_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.user_cell, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val discussion = discussions[position]
        updateItem(holder, discussion)
        onClick(holder, discussion)
    }

    private fun onClick(holder: ViewHolder, user: User) {
        holder.cardView.setOnLongClickListener {
            clickListener.onLongClick(user)
            true
        }

        holder.cardView.setOnClickListener {
            clickListener.onClick(user)
        }
    }

    private fun updateItem(holder: ViewHolder, user: User) {
        holder.nameDiscussion.text = user.userName
        Glide.with(context).load(user.urlImage).apply(RequestOptions.circleCropTransform()).into(holder.imageDiscussion)
    }
}
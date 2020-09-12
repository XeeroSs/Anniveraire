package com.xeross.anniveraire.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.xeross.anniveraire.R
import com.xeross.anniveraire.model.Message
import kotlinx.android.synthetic.main.message_cell.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(options: FirestoreRecyclerOptions<Message>,
                     private val glide: RequestManager,
                     private val idCurrentUser: String) :
        FirestoreRecyclerAdapter<Message, MessageAdapter.ViewHolder>(options) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val rootView: RelativeLayout = itemView.message_cell_root_view
        val profileContainer: LinearLayout = itemView.message_cell_item_profile_container
        val imageViewProfile: ImageView = itemView.message_cell_item_profile_container_profile_image
        val messageContainer: RelativeLayout = itemView.message_cell_item_message_container
        val cardViewImageSent: CardView = itemView.message_cell_item_message_container_image_sent_cardview
        val imageViewSent: ImageView = itemView.message_cell_item_message_container_image_sent_cardview_image
        val textMessageContainer: LinearLayout = itemView.message_cell_item_message_container_text_message_container
        val textViewMessage: TextView = itemView.message_cell_item_message_container_text_message_container_text_view
        val textViewDate: TextView = itemView.message_cell_item_message_container_text_view_date

        //FOR DATA
        private val colorCurrentUser: Int
        private val colorRemoteUser: Int
        fun updateWithMessage(message: Message, currentUserId: String?, glide: RequestManager) {

            // Check if current user is the sender
            val isCurrentUser: Boolean = message.userSender?.id.equals(currentUserId)

            // Update message TextView
            textViewMessage.text = message.message
            textViewMessage.textAlignment = if (isCurrentUser) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START

            // Update date TextView
            message.getDateCreated()?.let {
                textViewDate.text = convertDateToHour(it)
            }

            // Update profile picture ImageView
            message.userSender?.urlImage?.let {
                glide.load(it).apply(RequestOptions.circleCropTransform()).into(imageViewProfile)
            }

            // Update image sent ImageView
            if (message.urlImage != null) {
                glide.load(message.urlImage)
                        .into(imageViewSent)
                imageViewSent.visibility = View.VISIBLE
            } else {
                imageViewSent.visibility = View.GONE
            }

            //Update Message Bubble Color Background
            (textMessageContainer.background as GradientDrawable).setColor(if (isCurrentUser) colorCurrentUser else colorRemoteUser)

            // Update all views alignment depending is current user or not
            updateDesignDependingUser(isCurrentUser)
        }

        private fun updateDesignDependingUser(isSender: Boolean) {

            // PROFILE CONTAINER
            val paramsLayoutHeader = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            paramsLayoutHeader.addRule(if (isSender) RelativeLayout.ALIGN_PARENT_RIGHT else RelativeLayout.ALIGN_PARENT_LEFT)
            profileContainer.layoutParams = paramsLayoutHeader

            // MESSAGE CONTAINER
            val paramsLayoutContent = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            paramsLayoutContent.addRule(if (isSender) RelativeLayout.LEFT_OF else RelativeLayout.RIGHT_OF,
                    R.id.message_cell_item_profile_container)
            messageContainer.layoutParams = paramsLayoutContent

            // CARDVIEW IMAGE SEND
            val paramsImageView = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            paramsImageView.addRule(if (isSender) RelativeLayout.ALIGN_LEFT else RelativeLayout.ALIGN_RIGHT, R.id.message_cell_item_message_container_text_message_container)
            cardViewImageSent.layoutParams = paramsImageView
            rootView.requestLayout()
        }

        // ---
        private fun convertDateToHour(date: Date): String {
            val dfTime: DateFormat = SimpleDateFormat("HH:mm")
            return dfTime.format(date)
        }

        init {
            colorCurrentUser = ContextCompat.getColor(itemView.context, R.color.colorAccent)
            colorRemoteUser = ContextCompat.getColor(itemView.context, R.color.colorPrimary)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
        holder.updateWithMessage(model, this.idCurrentUser, this.glide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_cell, parent, false))
}
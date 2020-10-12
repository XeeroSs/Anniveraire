package com.xeross.anniveraire.adapter

import android.graphics.drawable.GradientDrawable
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
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
        val imageProfileContainer: RelativeLayout = itemView.container_image_profile
        val imageProfile: ImageView = itemView.image_profile
        val messageAndImageSentContainer: RelativeLayout = itemView.container_message_and_image_sent
        val userNameContainer: RelativeLayout = itemView.container_name_user
        val userName: TextView = itemView.name_user
        val messageContainer: RelativeLayout = itemView.container_message
        val message: TextView = itemView.message
        val messageSentContainer: RelativeLayout = itemView.container_image_sent
        val imageSent: ImageView = itemView.image_sent
        val dateContainer: RelativeLayout = itemView.container_date
        val date: TextView = itemView.date

        //FOR DATA
        private val colorCurrentUser: Int = ContextCompat.getColor(itemView.context,
                R.color.colorPrimaryDark)
        private val colorRemoteUser: Int = ContextCompat.getColor(itemView.context,
                R.color.colorGrey)
        private val colorTextCurrentUser: Int = ContextCompat.getColor(itemView.context,
                R.color.colorWhite)
        private val colorTextRemoteUser: Int = ContextCompat.getColor(itemView.context,
                R.color.colorBlack)

        fun updateWithMessage(messageObject: Message, currentUserId: String?, glide: RequestManager) {

            // Check if current user is the sender
            val isCurrentUser: Boolean = messageObject.userSender?.id.equals(currentUserId)

            // Update message TextView
            message.text = messageObject.message
            userName.text = messageObject.userSender?.userName
            message.setTextColor(if (isCurrentUser) colorTextCurrentUser else colorTextRemoteUser)
            //message.textAlignment = if (isCurrentUser) View.TEXT_ALIGNMENT_TEXT_END
            //else View.TEXT_ALIGNMENT_TEXT_START

            // Update date TextView
            messageObject.getDateCreated()?.let {
                date.text = convertDateToHour(it)
            }

            // Update profile picture ImageView
            messageObject.userSender?.urlImage?.let {
                glide.load(it).apply(RequestOptions.circleCropTransform()).into(imageProfile)
            }

            // Update image sent ImageView
            var hasImage = false
            messageObject.urlImage?.let {
                hasImage = true
                glide.load(it).into(imageSent)
                //   if (imageSent.background != null) {
                imageSent.visibility = View.VISIBLE
                // } else imageSent.visibility = View.GONE
            } ?: run {
                imageSent.visibility = View.GONE
            }

            //Update Message Bubble Color Background
            (messageContainer.background as GradientDrawable).setColor(if (isCurrentUser)
                colorCurrentUser else colorRemoteUser)

            // Update all views alignment depending is current user or not
            updateDesignDependingUser(isCurrentUser, hasImage)
        }

        private fun updateDesignDependingUser(isSender: Boolean, hasImage: Boolean) {

            // Image Profile Container
            val imageProfileContainerLayout = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            imageProfileContainerLayout.addRule(if (isSender) RelativeLayout.ALIGN_PARENT_END
            else RelativeLayout.ALIGN_PARENT_START)
            imageProfileContainerLayout.topMargin = 40
            imageProfileContainer.layoutParams = imageProfileContainerLayout

            // Message and Image sent Container
            val messageAndImageSentContainerLayout = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            messageAndImageSentContainerLayout.addRule(if (isSender) RelativeLayout.ALIGN_PARENT_START
            else RelativeLayout.ALIGN_PARENT_END)
            messageAndImageSentContainerLayout.addRule(if (isSender) RelativeLayout.START_OF
            else RelativeLayout.END_OF, R.id.container_image_profile)
            if (isSender) messageAndImageSentContainerLayout.marginEnd = 40 else
                messageAndImageSentContainerLayout.marginStart = 40
            messageAndImageSentContainer.layoutParams = messageAndImageSentContainerLayout

            // Username Container
            val userNameContainerLayout = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            userNameContainerLayout.addRule(if (isSender) RelativeLayout.ALIGN_PARENT_END
            else RelativeLayout.ALIGN_PARENT_START)
            userNameContainerLayout.bottomMargin = 15
            if (isSender) userNameContainerLayout.marginEnd = 30 else
                userNameContainerLayout.marginStart = 30
            userNameContainer.layoutParams = userNameContainerLayout

            // Message Container
            val messageContainerLayout = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            messageContainerLayout.addRule(if (isSender) RelativeLayout.ALIGN_PARENT_END
            else RelativeLayout.ALIGN_PARENT_START)
            messageContainerLayout.addRule(RelativeLayout.BELOW, R.id.container_name_user)
            messageContainer.layoutParams = messageContainerLayout

            // Image sent Container
            //  if (hasImage) {
            val messageSentContainerLayout = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            messageSentContainerLayout.addRule(if (isSender) RelativeLayout.ALIGN_PARENT_END
            else RelativeLayout.ALIGN_PARENT_START)
            messageSentContainerLayout.topMargin = 20
            messageSentContainerLayout.addRule(RelativeLayout.BELOW, R.id.container_message)
            messageSentContainer.layoutParams = messageSentContainerLayout
            //}

            // Date Container
            val dateContainerLayout = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            dateContainerLayout.addRule(if (isSender) RelativeLayout.ALIGN_PARENT_END
            else RelativeLayout.ALIGN_PARENT_START)
            if (isSender) dateContainerLayout.marginEnd = 30 else
                dateContainerLayout.marginStart = 30
            dateContainerLayout.topMargin = 15
            if (hasImage) {
                dateContainerLayout.addRule(RelativeLayout.BELOW, R.id.container_image_sent)
            } else {
                dateContainerLayout.addRule(RelativeLayout.BELOW, R.id.container_message)
            }
            dateContainer.layoutParams = dateContainerLayout

            rootView.requestLayout()
        }

        // ---
        private fun convertDateToHour(date: Date): String {
            if (!DateUtils.isToday(date.time)) {
                val dfTime: DateFormat = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.ENGLISH)
                return dfTime.format(date)
            }
            val dfTime: DateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            return dfTime.format(date)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
        holder.updateWithMessage(model, this.idCurrentUser, this.glide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_cell, parent, false))
}
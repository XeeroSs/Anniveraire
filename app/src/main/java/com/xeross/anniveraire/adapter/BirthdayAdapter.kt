package com.xeross.anniveraire.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xeross.anniveraire.R
import com.xeross.anniveraire.listener.ClickListener
import com.xeross.anniveraire.model.Birthday
import com.xeross.anniveraire.model.BirthdayState
import com.xeross.anniveraire.utils.UtilsDate
import kotlinx.android.synthetic.main.birthday_cell.view.*
import java.util.*
import kotlin.collections.ArrayList

class BirthdayAdapter(
    private val context: Context?,
    private var birthdays: ArrayList<Birthday>?,
    private val dateToday: Date,
    private val clickListener: ClickListener<Birthday>
) : RecyclerView.Adapter<BirthdayAdapter.EventViewHolder>(), Filterable {

    private var eventsFiltered = birthdays

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EventViewHolder(LayoutInflater.from(context).inflate(R.layout.birthday_cell, parent, false))

    override fun getItemCount() = birthdays?.let { return it.size } ?: 0

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        context?.let { contextMain ->
            birthdays?.let {
                val birthday = it[position]
                updateItem(contextMain, birthday, holder)
                onClick(holder, birthday)
            }
        }
    }

    fun updateList(birthdays: ArrayList<Birthday>?) {
        eventsFiltered = birthdays
    }

    private fun onClick(
        holder: EventViewHolder,
        birthday: Birthday
    ) {
        holder.cardView.setOnLongClickListener {
            clickListener.onLongClick(birthday)
            true
        }
    }

    private fun updateItem(contextMain: Context, birthday: Birthday, holder: EventViewHolder) {
        holder.nameEvent.text = if (birthday.lastName == "") birthday.firstName else
            contextMain.getString(
                R.string.firstname_lastname_event,
                birthday.firstName,
                birthday.lastName
            )
        holder.remainingDaysEvent.text = contextMain.getString(
            R.string.remaining_days,
            UtilsDate.getRemainingDays(dateToday, birthday.dateBirth)
        )
        when (birthday.state) {
            BirthdayState.BIRTHDAY -> {
                eventBirthday(holder, birthday, contextMain, R.drawable.im_birthday_cake)
            }
            BirthdayState.EVENT_BIRTHDAY -> {
                eventBirthday(holder, birthday, contextMain, R.drawable.im_calendar_event)
                holder.remainingDaysEvent.setTextColor(contextMain.resources.getColor(R.color.colorPrimary))
            }
            BirthdayState.OTHER -> {
                Glide.with(contextMain).load(R.drawable.im_champagne).into(holder.imageEvent)
                holder.dateEvent.text =
                    UtilsDate.getDateWithoutYearInString(birthday.dateBirth, contextMain)
                holder.ageEvent.visibility = View.GONE
            }
        }
    }

    private fun eventBirthday(
        holder: EventViewHolder,
        birthday: Birthday,
        contextMain: Context,
        imageDrawable: Int
    ) {
        Glide.with(contextMain).load(imageDrawable).into(holder.imageEvent)
        holder.dateEvent.text = UtilsDate.getDateInString(birthday.dateBirth, contextMain)
        holder.ageEvent.text = contextMain.getString(
            R.string.age_event,
            UtilsDate.getAgeEvent(dateToday, birthday.dateBirth).plus(1)
        )
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageEvent: ImageView = itemView.birthday_cell_image
        val nameEvent: TextView = itemView.birthday_cell_name
        val dateEvent: TextView = itemView.birthday_cell_date
        val ageEvent: TextView = itemView.birthday_cell_age
        val cardView: CardView = itemView.birthday_cell_item
        val remainingDaysEvent: TextView = itemView.birthday_cell_remaining_days
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterPattern =
                    charSequence.toString().toLowerCase(Locale.getDefault()).trim { it <= ' ' }
                val filteredList: MutableList<Birthday> = ArrayList()
                if (filterPattern.isEmpty()) {
                    birthdays?.let { filteredList.addAll(it) }
                } else {
                    eventsFiltered?.let {
                        for (event in it) {
                            if (event.firstName.contains(charSequence) ||
                                event.lastName.contains(charSequence)
                                || event.dateBirth.toString().contains(charSequence)
                            ) {
                                filteredList.add(event)
                            }
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                birthdays?.clear()
                birthdays?.addAll(filterResults.values as List<Birthday>)
                notifyDataSetChanged()
            }
        }
    }
}

package com.xeross.anniveraire.adapter

import android.content.Context
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.xeross.anniveraire.listener.ClickListener
import java.util.*

abstract class DiscussionAdapter<VH : RecyclerView.ViewHolder, T>(
        private val objectList: ArrayList<T>,
        private val objectListFull: ArrayList<T>,
        protected val context: Context,
        protected val dateToday: Date,
        protected val clickListener: ClickListener<T>) : RecyclerView.Adapter<VH>(), Filterable {

    override fun getItemCount() = objectList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val dObject = objectList[position]
        updateItem(holder, dObject)
        onClick(holder, dObject)
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filteredList: MutableList<T> = ArrayList()
                if (constraint.isEmpty()) filteredList.addAll(objectListFull) else {
                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }
                    objectListFull.forEach { if (filterItem(it, filterPattern)) filteredList.add(it) }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                objectList.clear()
                @Suppress("UNCHECKED_CAST")
                objectList.addAll(results.values as List<T>)
                notifyDataSetChanged()
            }
        }
    }

    abstract fun onClick(holder: VH, dObject: T)

    abstract fun updateItem(holder: VH, dObject: T)

    abstract fun filterItem(dObject: T, filterPattern: String): Boolean

    protected fun String.containsString(string: String): Boolean =
            this.toLowerCase(Locale.ROOT).contains(string)
}
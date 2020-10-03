package com.xeross.anniveraire.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.xeross.anniveraire.R
import com.xeross.anniveraire.listener.ClickListener
import kotlinx.android.synthetic.main.gallery_cell.view.*
import java.util.*

class GalleryAdapter(private val discussions: ArrayList<String>,
                     private val context: Context, private val clickListener: ClickListener<String>) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    override fun getItemCount() = discussions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.gallery_cell_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.gallery_cell, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = discussions[position]
        updateItem(holder, url)
        holder.image.setOnClickListener {
            clickListener.onClick(url)
        }
    }

    private fun updateItem(holder: ViewHolder, url: String) {
        Glide.with(context).load(url)
                .thumbnail(0.5f)
                .useAnimationPool(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image)
    }
}
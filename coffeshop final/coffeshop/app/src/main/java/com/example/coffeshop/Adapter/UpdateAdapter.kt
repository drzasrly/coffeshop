// UpdateAdapter.kt
package com.example.coffeshop.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeshop.R

// Kita gunakan list string saja dulu untuk URL gambarnya
class UpdateAdapter(private val items: List<String>) : RecyclerView.Adapter<UpdateAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgUpdate: ImageView = itemView.findViewById(R.id.imgUpdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Buat file layout kecil 'item_update.xml' (isi cuma ImageView rounded)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_update, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(items[position])
            .into(holder.imgUpdate)
    }

    override fun getItemCount(): Int = items.size
}
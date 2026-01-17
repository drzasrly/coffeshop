package com.example.coffeshop.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeshop.Activity.DetailActivity
import com.example.coffeshop.Domain.ItemsModel
import com.example.coffeshop.databinding.ViewholderPopularBinding

class PopularAdapter(
    private val items: MutableList<ItemsModel>
) : RecyclerView.Adapter<PopularAdapter.Viewholder>() {

    private lateinit var context: Context

    class Viewholder(val binding: ViewholderPopularBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding = ViewholderPopularBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]

        holder.binding.titleTxt.text = item.title
        holder.binding.priceTxt.text = "Rp ${item.price}"
        holder.binding.subtitleTxt.text = item.extra

        // PERBAIKAN: Langsung gunakan item.picUrl karena sekarang tipenya sudah String
        Glide.with(context)
            .load(item.picUrl)
            .into(holder.binding.pic)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            item.id = position
            intent.putExtra("object", item)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}
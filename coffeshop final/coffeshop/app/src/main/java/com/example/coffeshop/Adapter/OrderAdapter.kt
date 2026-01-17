package com.example.coffeshop.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeshop.Domain.OrderModel
import com.example.coffeshop.R
import com.example.coffeshop.databinding.ViewholderOrderItemBinding

class OrderAdapter(private val orders: List<OrderModel>) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderOrderItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderOrderItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        val context = holder.itemView.context

        with(holder.binding) {
            dateTxt.text = order.date

            // Tampilan Status Aktif
            statusTxt.text = order.status
            statusTxt.background = null

            // Logika Warna Status
            when (order.status) {
                "Selesai" -> statusTxt.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                "Proses", "Diproses Admin" -> statusTxt.setTextColor(ContextCompat.getColor(context, R.color.orange))
                else -> statusTxt.setTextColor(ContextCompat.getColor(context, R.color.darkBrown))
            }

            // Menampilkan Harga dari Database (Bukan Rp 0)
            totalPriceTxt.text = "Rp ${String.format("%,.0f", order.totalPrice)}"

            if (!order.items.isNullOrEmpty()) {
                // PERBAIKAN: Menampilkan semua nama produk yang dibeli
                // Contoh: "Cappuccino, Espresso"
                val allProductNames = order.items.joinToString(", ") { it.title }
                titleTxt.text = allProductNames

                // Menghitung total cup
                val totalQty = order.items.sumOf { it.quantity }
                qtyInfoTxt.text = "$totalQty Item"

                // Menggunakan gambar produk pertama sebagai ikon
                Glide.with(context).load(order.items[0].picUrl).into(picOrder)
            }

            finishBtn.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = orders.size
}
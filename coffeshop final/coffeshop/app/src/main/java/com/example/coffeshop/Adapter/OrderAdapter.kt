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
import java.text.NumberFormat
import java.util.Locale

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
            // Set Tanggal & Status
            dateTxt.text = order.date
            statusTxt.text = order.status

            // Warna Status berdasarkan gambar image_62d9bb.png
            when (order.status) {
                "Selesai" -> statusTxt.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                "Proses" -> statusTxt.setTextColor(ContextCompat.getColor(context, R.color.orange))
                else -> statusTxt.setTextColor(ContextCompat.getColor(context, R.color.orange))
            }

            // Set Total Harga agar tidak Rp 0
            totalPriceTxt.text = formatRupiah(order.totalPrice)

            // Mengisi Judul, Jumlah, dan Gambar jika item tersedia
            if (!order.items.isNullOrEmpty()) {
                titleTxt.text = order.items.joinToString(", ") { it.title }
                qtyInfoTxt.text = "${order.items.sumOf { it.quantity }} Item"

                // Memunculkan Gambar menggunakan Glide (Sesuai picUrl di Firebase)
                Glide.with(context)
                    .load(order.items[0].picUrl)
                    .placeholder(R.drawable.btn_3) // Menggunakan aset btn_3 dari drawable kamu
                    .into(picOrder)
            } else {
                titleTxt.text = "Detail Pesanan Kosong"
                qtyInfoTxt.text = "0 Item"
            }

            // Tombol pesanan diterima (hidden by default)
            finishBtn.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = orders.size

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number).replace("Rp", "Rp ")
    }
}
package com.example.coffeshop.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeshop.Domain.OrderItemModel
import com.example.coffeshop.R
import com.example.coffeshop.databinding.ViewholderOrderItemBinding // Gunakan viewholder yang sama atau buat baru jika ingin desain berbeda

class OrderDetailItemAdapter(private val items: List<OrderItemModel>) :
    RecyclerView.Adapter<OrderDetailItemAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderOrderItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderOrderItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            // Bind data item produk
            titleTxt.text = item.title
            qtyInfoTxt.text = "${item.quantity} Item (${item.size})"

            // Format harga item x quantity
            val itemTotal = item.price * item.quantity
            totalPriceTxt.text = "Rp " + String.format("%,.0f", itemTotal).replace(",", ".")

            // Memunculkan gambar menggunakan Glide
            Glide.with(holder.itemView.context)
                .load(item.picUrl)
                .placeholder(R.drawable.btn_3)
                .into(picOrder)

            // Sembunyikan garis pemisah jika diperlukan
            divider.alpha = 0.3f
        }
    }

    override fun getItemCount(): Int = items.size
}
package com.example.coffeshop.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeshop.Domain.CartModel
import com.example.coffeshop.databinding.ViewholderCheckoutBinding

class CheckoutAdapter(private val list: List<CartModel>) :
    RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    // Menggunakan ViewholderCheckoutBinding yang merujuk ke viewholder_checkout.xml
    class ViewHolder(val binding: ViewholderCheckoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderCheckoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.apply {
            // Set Nama Produk dan Ukuran
            titleTxt.text = "${item.name} (${item.size})"

            // Set Jumlah (format: x2)
            qtyTxt.text = "x${item.quantity}"

            // Bersihkan harga dan hitung total per item (Harga Satuan x Jumlah)
            val cleanPrice = item.price.replace(Regex("[^\\d.]"), "").toDoubleOrNull() ?: 0.0
            val totalPriceForItem = cleanPrice * item.quantity
            totalEachItem.text = "Rp ${totalPriceForItem.toInt()}"

            // Load Gambar menggunakan Glide
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .into(picCart)
        }
    }

    override fun getItemCount(): Int = list.size
}
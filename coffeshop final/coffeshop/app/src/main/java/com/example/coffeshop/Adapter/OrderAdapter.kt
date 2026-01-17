package com.example.coffeshop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeshop.Domain.OrderModel
import com.example.coffeshop.databinding.ViewholderOrderItemBinding
import com.google.firebase.database.FirebaseDatabase

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
            // 1. Set Data Dasar
            dateTxt.text = order.date
            statusTxt.text = order.status
            totalPriceTxt.text = "Rp ${order.totalPrice}"

            // 2. Logika Tampilan Item (Judul & Gambar)
            if (order.items.isNotEmpty()) {
                val firstItem = order.items[0]

                // Menampilkan nama produk pertama + info tambahan jika lebih dari 1
                titleTxt.text = if (order.items.size > 1) {
                    "${firstItem.title} & ${order.items.size - 1} lainnya"
                } else {
                    firstItem.title
                }

                qtyInfoTxt.text = "${order.items.size} Item"

                // PERBAIKAN GLIDE: Menggunakan link String langsung
                Glide.with(context)
                    .load(firstItem.picUrl)
                    .into(picOrder)
            }

            // 3. Logika Tombol "Pesanan Diterima" (Update Status)
            // Tombol hanya muncul jika pesanan belum "Selesai"
            if (order.status == "Selesai") {
                finishBtn.visibility = View.GONE
            } else {
                finishBtn.visibility = View.VISIBLE
            }

            finishBtn.setOnClickListener {
                updateOrderStatusInFirebase(order.orderId, context)
            }
        }
    }

    /**
     * Fungsi untuk memperbarui field 'status' pada pesanan tertentu di Firebase
     */
    private fun updateOrderStatusInFirebase(orderId: String, context: Context) {
        if (orderId.isEmpty()) {
            Toast.makeText(context, "ID Pesanan tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val dbRef = FirebaseDatabase.getInstance().getReference("Orders").child(orderId)

        // Hanya memperbarui bagian status saja
        val updates = mapOf<String, Any>("status" to "Selesai")

        dbRef.updateChildren(updates).addOnSuccessListener {
            Toast.makeText(context, "Pesanan Berhasil Diselesaikan!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Gagal memperbarui status: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = orders.size
}
package com.example.coffeshop.Activity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.OrderDetailItemAdapter
import com.example.coffeshop.Domain.OrderModel
import com.example.coffeshop.R
import com.example.coffeshop.databinding.ActivityOrderDetailBinding
import java.text.NumberFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cara aman mengambil data Parcelable untuk semua versi Android
        val order = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("order_data", OrderModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<OrderModel>("order_data")
        }

        // Jika data berhasil diterima, tampilkan ke UI
        order?.let {
            setupUI(it)
            setupRecyclerView(it)
        }

        // Navigasi kembali
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun setupUI(order: OrderModel) {
        with(binding) {
            // Menampilkan ID, Tanggal, dan Alamat
            orderIdTxt.text = "#${order.orderId}"
            dateTxt.text = order.date
            addressTxt.text = order.address

            // Menampilkan Status dengan logika warna yang sama dengan adapter
            statusTxt.text = order.status
            when (order.status) {
                "Selesai" -> statusTxt.setTextColor(ContextCompat.getColor(this@OrderDetailActivity, android.R.color.darker_gray))
                "Proses" -> statusTxt.setTextColor(ContextCompat.getColor(this@OrderDetailActivity, R.color.orange))
                else -> statusTxt.setTextColor(ContextCompat.getColor(this@OrderDetailActivity, R.color.darkBrown))
            }

            // Menampilkan Total Harga
            totalPriceTxt.text = formatRupiah(order.totalPrice)
        }
    }

    private fun setupRecyclerView(order: OrderModel) {
        // Menampilkan daftar item produk yang dibeli
        binding.itemListDetail.apply {
            layoutManager = LinearLayoutManager(this@OrderDetailActivity)
            // Pastikan items tidak null sebelum dipasang ke adapter
            adapter = OrderDetailItemAdapter(order.items ?: arrayListOf())
        }
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number).replace("Rp", "Rp ")
    }
}
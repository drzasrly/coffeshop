package com.example.coffeshop.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.OrderAdapter
import com.example.coffeshop.databinding.ActivityOrderBinding
import com.example.coffeshop.viewModel.OrderViewModel

/**
 * Activity untuk menampilkan daftar riwayat pesanan pengguna.
 * Mengambil data secara real-time dari Firebase agar status dari Admin Filament tersinkronisasi.
 */
class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding sesuai dengan activity_order.xml
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ViewModel untuk mengelola data pesanan
        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        setupRecyclerView()
        observeOrders()

        // Handler untuk tombol kembali menggunakan ID backBtn dari XML Anda
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    /**
     * Mengatur konfigurasi RecyclerView untuk menampilkan daftar pesanan.
     */
    private fun setupRecyclerView() {
        // Menggunakan ID orderRecyclerView sesuai dengan file XML Anda
        binding.orderRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Mengamati perubahan data pesanan dari Firebase melalui ViewModel.
     */
    private fun observeOrders() {
        // Memicu fungsi pengambilan data dari Firebase Realtime Database
        orderViewModel.fetchOrdersFromFirebase()

        // Mendengarkan perubahan pada LiveData firebaseOrders
        orderViewModel.firebaseOrders.observe(this) { orders ->
            if (orders != null) {
                // Menghubungkan adapter dengan data pesanan terbaru
                // Menggunakan OrderAdapter yang telah disesuaikan sebelumnya
                val adapter = OrderAdapter(orders)
                binding.orderRecyclerView.adapter = adapter
            }
        }
    }
}
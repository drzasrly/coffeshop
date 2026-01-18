package com.example.coffeshop.Activity

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.CheckoutAdapter
import com.example.coffeshop.Domain.*
import com.example.coffeshop.databinding.ActivityCheckoutBinding
import com.example.coffeshop.viewModel.CartViewModel
import com.example.coffeshop.viewModel.OrderViewModel
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var cartViewModel: CartViewModel
    private var cartItems: ArrayList<CartModel> = arrayListOf()
    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ViewModel untuk logika keranjang dan pesanan
        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        setupData()
        setupRecyclerView()

        // Tombol kembali sesuai dengan ID di XML Anda
        binding.backBtn.setOnClickListener { finish() }

        // Listener tombol pembayaran
        binding.payBtn.setOnClickListener {
            if (!isProcessing) {
                val total = intent.getDoubleExtra("total", 0.0)
                processCheckout(total)
            }
        }
    }

    private fun setupData() {
        val total = intent.getDoubleExtra("total", 0.0)
        val subtotal = intent.getDoubleExtra("subtotal", 0.0)
        val tax = intent.getDoubleExtra("tax", 0.0)
        val delivery = intent.getDoubleExtra("delivery", 0.0)

        // Mengambil daftar item dari intent keranjang
        val items = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("cartItems", CartModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<CartModel>("cartItems")
        }

        items?.let { cartItems = it }

        binding.apply {
            totalTxt.text = formatRupiah(total)
            subtotalTxt.text = formatRupiah(subtotal)
            taxTxt.text = formatRupiah(tax)
            deliveryTxt.text = formatRupiah(delivery)
        }
    }

    private fun setupRecyclerView() {
        binding.checkoutItemList.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = CheckoutAdapter(cartItems)
        }
    }

    private fun processCheckout(totalPrice: Double) {
        val address = binding.addressEdt.text.toString().trim()
        if (address.isEmpty()) {
            Toast.makeText(this, "Alamat wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        isProcessing = true
        binding.payBtn.isEnabled = false
        binding.payBtn.text = "Sedang Memproses..."

        // Menyiapkan data item untuk dikirim ke MySQL (Laravel API)
        val itemsForApi = cartItems.map {
            val cleanPrice = it.price.replace(Regex("[^\\d]"), "").toDoubleOrNull() ?: 0.0
            OrderItemRequest(it.menu_id, it.quantity, cleanPrice, it.size)
        }

        // 1. Simpan ke MySQL via API Laravel untuk mendapatkan ID resmi
        orderViewModel.checkout(itemsForApi, totalPrice, address,
            onSuccess = { orderId ->
                // 2. Gunakan ID MySQL (misal: 118) sebagai kunci Firebase
                pushOrderToFirebase(totalPrice, address, orderId.toString())
            },
            onError = { error ->
                isProcessing = false
                binding.payBtn.isEnabled = true
                binding.payBtn.text = "Proses Pembayaran"
                // Mengatasi pesan error jika struktur data server tidak sesuai
                Toast.makeText(this, "Gagal: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun pushOrderToFirebase(totalPrice: Double, address: String, orderId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Orders")

        // Konversi item keranjang ke model pesanan Firebase agar detail produk muncul
        val itemsList = ArrayList<OrderItemModel>()
        cartItems.forEach {
            val cleanPrice = it.price.replace(Regex("[^\\d]"), "").toDoubleOrNull() ?: 0.0
            itemsList.add(OrderItemModel(
                title = it.name,
                price = cleanPrice,
                quantity = it.quantity,
                picUrl = it.imageUrl, // Pastikan picUrl tersimpan agar gambar muncul di riwayat
                size = it.size
            ))
        }

        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val currentTimestamp = System.currentTimeMillis()

        // Membuat objek OrderModel lengkap dengan ID MySQL dan daftar produk
        val newOrder = OrderModel(
            orderId = orderId,
            date = currentDate,
            status = "Proses",
            totalPrice = totalPrice, // Memastikan harga tidak Rp 0
            address = address,
            timestamp = currentTimestamp,
            items = itemsList // Pastikan daftar produk terkirim agar tidak "Detail Pesanan Kosong"
        )

        // Simpan ke Firebase Realtime Database
        dbRef.child(orderId).setValue(newOrder).addOnSuccessListener {
            // Hapus keranjang hanya jika semua proses sukses
            cartViewModel.clearSelectedItems(cartItems)
            runOnUiThread {
                Toast.makeText(this, "Pesanan Berhasil Dicatat!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener { e ->
            isProcessing = false
            binding.payBtn.isEnabled = true
            Toast.makeText(this, "Firebase Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number).replace("Rp", "Rp ")
    }
}
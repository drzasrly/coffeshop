package com.example.coffeshop.Activity

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.CheckoutAdapter
import com.example.coffeshop.Domain.CartModel
import com.example.coffeshop.Domain.OrderItemRequest
import com.example.coffeshop.databinding.ActivityCheckoutBinding
import com.example.coffeshop.viewModel.CartViewModel
import com.example.coffeshop.viewModel.OrderViewModel
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var orderViewModel: OrderViewModel
    // 1. Tambahkan variabel untuk CartViewModel
    private lateinit var cartViewModel: CartViewModel
    private var cartItems: ArrayList<CartModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Inisialisasi kedua ViewModel
        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        setupData()
        setupRecyclerView()

        binding.backBtn.setOnClickListener { finish() }

        binding.payBtn.setOnClickListener {
            val total = intent.getDoubleExtra("total", 0.0)
            checkout(total)
        }
    }

    private fun setupData() {
        val subtotal = intent.getDoubleExtra("subtotal", 0.0)
        val tax = intent.getDoubleExtra("tax", 0.0)
        val delivery = intent.getDoubleExtra("delivery", 0.0)
        val total = intent.getDoubleExtra("total", 0.0)

        val items = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("cartItems", CartModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<CartModel>("cartItems")
        }

        if (items != null) {
            cartItems = items
        }

        setSummary(subtotal, tax, delivery, total)
    }

    private fun setupRecyclerView() {
        val checkoutAdapter = CheckoutAdapter(cartItems)
        binding.checkoutItemList.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = checkoutAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setSummary(subtotal: Double, tax: Double, delivery: Double, total: Double) {
        binding.apply {
            subtotalTxt.text = formatRupiah(subtotal)
            taxTxt.text = formatRupiah(tax)
            deliveryTxt.text = formatRupiah(delivery)
            totalTxt.text = formatRupiah(total)
        }
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number).replace("Rp", "Rp ")
    }

    private fun checkout(totalPrice: Double) {
        val address = binding.addressEdt.text.toString().trim()
        if (address.isEmpty()) {
            Toast.makeText(this, "Alamat wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val itemsForApi = cartItems.map {
            val cleanPrice = it.price.replace(Regex("[^\\d]"), "").toDoubleOrNull() ?: 0.0
            OrderItemRequest(
                menu_id = it.menu_id,
                quantity = it.quantity,
                price = cleanPrice,
                size = it.size
            )
        }

        binding.payBtn.isEnabled = false
        binding.payBtn.text = "Memproses..."


        orderViewModel.checkout(itemsForApi, totalPrice, address,
            onSuccess = {
                // 3. JALANKAN PENGHAPUSAN DI SINI SETELAH BERHASIL KE LARAVEL
                // Menghapus hanya item yang tadi di-checkout dari database lokal (Room)
                cartViewModel.clearSelectedItems(cartItems)

                runOnUiThread {
                    Toast.makeText(this, "Pesanan Berhasil & Keranjang Diperbarui!", Toast.LENGTH_LONG).show()
                    finish()
                }
            },
            onError = { error ->
                runOnUiThread {
                    binding.payBtn.isEnabled = true
                    binding.payBtn.text = "Proses Pembayaran"
                    Toast.makeText(this, "Gagal: $error", Toast.LENGTH_SHORT).show()
                }
            }


        )

    }
}
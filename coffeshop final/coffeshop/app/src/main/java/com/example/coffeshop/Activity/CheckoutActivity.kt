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
import com.example.coffeshop.Domain.OrderItemModel
import com.example.coffeshop.Domain.OrderModel
import com.example.coffeshop.Domain.OrderItemRequest
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

    // Variabel pengunci untuk mencegah klik ganda (Anti-Duplikasi)
    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        setupData()
        setupRecyclerView()

        binding.backBtn.setOnClickListener { finish() }

        binding.payBtn.setOnClickListener {
            if (!isProcessing) {
                val total = intent.getDoubleExtra("total", 0.0)
                checkout(total)
            }
        }
    }

    private fun setupData() {
        val total = intent.getDoubleExtra("total", 0.0)
        val subtotal = intent.getDoubleExtra("subtotal", 0.0)
        val tax = intent.getDoubleExtra("tax", 0.0)
        val delivery = intent.getDoubleExtra("delivery", 0.0)

        val items = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("cartItems", CartModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<CartModel>("cartItems")
        }

        items?.let { cartItems = it }
        setSummary(subtotal, tax, delivery, total)
    }

    private fun setupRecyclerView() {
        binding.checkoutItemList.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = CheckoutAdapter(cartItems)
            isNestedScrollingEnabled = false
        }
    }

    private fun checkout(totalPrice: Double) {
        val address = binding.addressEdt.text.toString().trim()
        if (address.isEmpty()) {
            Toast.makeText(this, "Alamat wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Aktifkan pengunci dan ubah UI
        isProcessing = true
        binding.payBtn.isEnabled = false
        binding.payBtn.text = "Memproses..."

        val itemsForApi = cartItems.map {
            val cleanPrice = it.price.replace(Regex("[^\\d]"), "").toDoubleOrNull() ?: 0.0
            OrderItemRequest(it.menu_id, it.quantity, cleanPrice, it.size)
        }

        orderViewModel.checkout(itemsForApi, totalPrice, address,
            onSuccess = {
                // Kirim ke Firebase hanya jika API Laravel Berhasil
                pushOrderToFirebase(totalPrice, address)
            },
            onError = { error ->
                runOnUiThread {
                    // Buka kunci jika gagal agar user bisa mencoba lagi
                    isProcessing = false
                    binding.payBtn.isEnabled = true
                    binding.payBtn.text = "Proses Pembayaran"
                    Toast.makeText(this, "Gagal API: $error", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun pushOrderToFirebase(totalPrice: Double, address: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Orders")
        val orderId = dbRef.push().key ?: return

        val itemsList = ArrayList<OrderItemModel>()
        cartItems.forEach {
            val cleanPrice = it.price.replace(Regex("[^\\d]"), "").toDoubleOrNull() ?: 0.0
            itemsList.add(
                OrderItemModel(
                    title = it.name,
                    price = cleanPrice,
                    quantity = it.quantity,
                    picUrl = it.imageUrl,
                    size = it.size
                )
            )
        }

        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        val newOrder = OrderModel(
            orderId = orderId,
            date = currentDate,
            status = "Proses",
            totalPrice = totalPrice,
            address = address,
            items = itemsList
        )

        dbRef.child(orderId).setValue(newOrder).addOnSuccessListener {
            cartViewModel.clearSelectedItems(cartItems)
            runOnUiThread {
                Toast.makeText(this, "Pesanan Berhasil Dicatat!", Toast.LENGTH_LONG).show()
                finish()
            }
        }.addOnFailureListener { e ->
            runOnUiThread {
                isProcessing = false
                binding.payBtn.isEnabled = true
                binding.payBtn.text = "Proses Pembayaran"
                Toast.makeText(this, "Gagal Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
}
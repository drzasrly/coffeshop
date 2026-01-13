package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.CartAdapter
import com.example.coffeshop.Domain.CartModel
import com.example.coffeshop.databinding.ActivityCartBinding
import com.example.coffeshop.viewModel.CartViewModel

class CartActivity : AppCompatActivity() {
    private var subtotalValue = 0.0
    private var taxValue = 0.0
    private var deliveryValue = 15000.0 // Contoh biaya ongkir Rp 15.000
    private var totalValue = 0.0
    private var itemList: ArrayList<CartModel> = arrayListOf()

    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        initCartList()

        // Tombol Kembali
        binding.backBtn.setOnClickListener { finish() }

        // Tombol Checkout
        binding.tocheckout.setOnClickListener {
            // Filter hanya item yang dicentang (isSelected == true)
            val selectedItems = itemList.filter { it.isSelected }

            if (selectedItems.isNotEmpty()) {
                val intent = Intent(this, CheckoutActivity::class.java).apply {
                    putExtra("subtotal", subtotalValue)
                    putExtra("tax", taxValue)
                    putExtra("delivery", if (subtotalValue > 0) deliveryValue else 0.0)
                    putExtra("total", totalValue)
                    // Mengirimkan list item yang hanya dipilih saja
                    putExtra("cartItems", ArrayList(selectedItems))
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Silakan pilih minimal satu item untuk checkout", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initCartList() {
        // Inisialisasi adapter dengan callback untuk hitung ulang saat checkbox diklik
        cartAdapter = CartAdapter(arrayListOf(), this, viewModel) {
            // Ini dipanggil setiap kali checkbox di item diklik
            calculateCartFromRoom(itemList)
        }

        binding.listView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.listView.adapter = cartAdapter

        // Mengobservasi data dari Room secara real-time
        viewModel.localCart.observe(this) { items ->
            if (items != null) {
                // Saat data baru datang dari database, kita update itemList
                // Catatan: Default isSelected biasanya false dari model
                itemList = ArrayList(items)
                cartAdapter.updateData(items)
                calculateCartFromRoom(items)
            }
        }
    }

    private fun calculateCartFromRoom(items: List<CartModel>) {
        val percentTax = 0.02 // Pajak 2%
        var fee = 0.0

        // LOGIKA UTAMA: Hanya hitung item yang isSelected == true
        val selectedItems = items.filter { it.isSelected }

        for (item in selectedItems) {
            // Bersihkan format harga (menghilangkan Rp atau titik jika ada)
            val cleanPrice = item.price.replace(Regex("[^\\d.]"), "").toDoubleOrNull() ?: 0.0
            fee += cleanPrice * item.quantity
        }

        // Jika tidak ada item dipilih, ongkir jadi 0
        val currentDelivery = if (selectedItems.isNotEmpty()) deliveryValue else 0.0

        val tax = fee * percentTax
        val total = fee + tax + currentDelivery

        // Simpan ke variabel global untuk dikirim ke CheckoutActivity
        subtotalValue = fee
        taxValue = tax
        totalValue = total

        // Update UI
        binding.totalFeeTxt.text = "Rp ${fee.toInt()}"
        binding.totalTaxTxt.text = "Rp ${tax.toInt()}"
        binding.deliveryTxt.text = "Rp ${currentDelivery.toInt()}"
        binding.totalTxt.text = "Rp ${total.toInt()}"
    }
}
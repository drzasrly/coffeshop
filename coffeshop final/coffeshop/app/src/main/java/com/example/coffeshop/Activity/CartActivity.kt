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
    private var deliveryValue = 15.0
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

        // Gunakan ViewModelProvider yang standar
        viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        initCartList()
        binding.backBtn.setOnClickListener { finish() }

        binding.tocheckout.setOnClickListener {
            if (itemList.isNotEmpty()) {
                val intent = Intent(this, CheckoutActivity::class.java).apply {
                    putExtra("subtotal", subtotalValue)
                    putExtra("tax", taxValue)
                    putExtra("delivery", deliveryValue)
                    putExtra("total", totalValue)
                    // Pastikan dikirim sebagai ArrayList
                    putExtra("cartItems", itemList)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initCartList() {
        cartAdapter = CartAdapter(arrayListOf(), this, viewModel)
        binding.listView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.listView.adapter = cartAdapter

        viewModel.localCart.observe(this) { items ->
            itemList = ArrayList(items)
            cartAdapter.updateData(items)
            calculateCartFromRoom(items)
        }
    }

    private fun calculateCartFromRoom(items: List<CartModel>) {
        val percentTax = 0.02
        var fee = 0.0
        for (item in items) {
            val priceValue = item.price.toDoubleOrNull() ?: 0.0
            fee += priceValue * item.quantity
        }
        val tax = fee * percentTax
        val total = fee + tax + deliveryValue

        subtotalValue = fee
        taxValue = tax
        totalValue = total

        binding.totalFeeTxt.text = "Rp ${fee.toInt()}"
        binding.totalTaxTxt.text = "Rp ${tax.toInt()}"
        binding.deliveryTxt.text = "Rp ${deliveryValue.toInt()}"
        binding.totalTxt.text = "Rp ${total.toInt()}"
    }
}
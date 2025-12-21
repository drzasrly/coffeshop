package com.example.coffeshop.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.CartAdapter
import com.example.coffeshop.Helper.ChangeNumberItemsListener
import com.example.coffeshop.Helper.ManagmentCart
import com.example.coffeshop.databinding.ActivityCartBinding
import com.example.coffeshop.viewModel.CartViewModel
import com.example.coffeshop.Domain.CartModel

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CartViewModel::class.java)

        setVariable()
        initCartList()
    }

    private fun initCartList() {
        binding.listView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Observe data dari Room
        viewModel.localCart.observe(this) { items ->
            // Update Adapter
            val adapter = CartAdapter(
                ArrayList(items),
                this,
                viewModel,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        // Perhitungan otomatis terpicu oleh LiveData
                    }
                }
            )
            binding.listView.adapter = adapter

            // Update Total Biaya
            calculateCartFromRoom(items)
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun calculateCartFromRoom(items: List<CartModel>) {
        val percentTax = 0.02
        val delivery = 15.0

        var fee = 0.0
        for (item in items) {
            val priceValue = item.price.toDoubleOrNull() ?: 0.0
            fee += (priceValue * item.quantity)
        }

        val tax = Math.round((fee * percentTax) * 100.0) / 100.0
        val subTotal = Math.round(fee * 100.0) / 100.0
        val total = Math.round((subTotal + tax + delivery) * 100.0) / 100.0

        binding.apply {
            totalFeeTxt.text = "$$subTotal"
            totalTaxTxt.text = "$$tax"
            deliveryTxt.text = "$$delivery"
            totalTxt.text = "$$total"
        }
    }
}
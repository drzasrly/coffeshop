package com.example.coffeshop.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.CartAdapter
import com.example.coffeshop.Helper.ChangeNumberItemsListener
import com.example.coffeshop.Helper.ManagmentCart
import com.example.coffeshop.R
import com.example.coffeshop.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    lateinit var binding: ActivityCartBinding
    private lateinit var managmentCart: ManagmentCart
    private var tax: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityCartBinding.inflate(layoutInflater)

        // PERBAIKAN 1: Menggunakan binding.root
        setContentView(binding.root)

        managmentCart= ManagmentCart(this)

        calculateCart()
        setVariable()
        initCartList()
    }

    private fun initCartList() {
        binding.apply {
            listView.layoutManager=
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)

            listView.adapter= CartAdapter(
                managmentCart.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        calculateCart()
                    }
                }
            )
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun calculateCart() {
        val percentTax= 0.02
        val delivery=15.0 // Mengubah menjadi Double untuk perhitungan yang konsisten

        // Menggunakan 100.0 untuk memastikan perhitungan Double
        val fee = managmentCart.getTotalFee()
        tax=((fee * percentTax) * 100)/100.0
        val total=((fee + tax + delivery) * 100)/100.0
        val itemtotal=(fee * 100)/100.0

        binding.apply {
            totalFeeTxt.text= "$$itemtotal"
            totalTaxTxt.text= "$$tax"
            deliveryTxt.text= "$$delivery"
            totalTxt.text= "$$total"
        }

    }
}
// Activity/MainActivity.kt
package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.coffeshop.Adapter.CategoryAdapter
import com.example.coffeshop.Adapter.PopularAdapter
import com.example.coffeshop.databinding.ActivityMainBinding
import com.example.coffeshop.viewModel.MainViewModel
import com.example.coffeshop.Helper.TokenManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🟢 CEK AUTH OTOMATIS:
        if (TokenManager.instance.getToken() == null || TokenManager.instance.isTokenExpired()) {
            TokenManager.instance.clearToken()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        displayUserName()
        observeBanner()
        initCategory()
        initPopular()
        initBottomMenu()
    }

    override fun onResume() {
        super.onResume()
        displayUserName()
    }

    private fun displayUserName() {
        val userName = TokenManager.instance.getUserName()
        Log.d("MAIN_ACTIVITY", "Nama yang diambil dari Manager: $userName")

        if (userName != null && userName.isNotEmpty()) {
            binding.textView2.text = userName
        } else {
            binding.textView2.text = "Guest"
        }
    }

    private fun initBottomMenu() {

        // 1. Tombol Keranjang (Cart)
        binding.cartBtn.setOnClickListener {
            if (TokenManager.instance.getToken() == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, CartActivity::class.java))
            }
        }

        // 2. Tombol Profil
        binding.profileBtn.setOnClickListener {
            if (TokenManager.instance.getToken() == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        }

        // 3. Tombol Wishlist
        binding.wishlistBtn.setOnClickListener {
            if (TokenManager.instance.getToken() == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, wishlistActivity::class.java))
            }
        }

        // 🟢 4. TOMBOL PESANAN (RIWAYAT TRANSAKSI)
        // Pastikan ID di XML MainActivity Anda adalah "pesananBtn"
        binding.pesananBtn.setOnClickListener {
            if (TokenManager.instance.getToken() == null) {
                // Jika Belum Login
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // Jika Sudah Login, buka OrderActivity
                startActivity(Intent(this, OrderActivity::class.java))
            }
        }
    }

    private fun initPopular() {
        binding.progressBarPopuler.visibility = View.VISIBLE
        viewModel.loadPopular().observe(this) { popularList ->
            binding.recyclerView2.layoutManager = GridLayoutManager(this@MainActivity, 2)
            binding.recyclerView2.adapter = PopularAdapter(popularList)
            binding.progressBarPopuler.visibility = View.GONE
        }
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.loadCategory().observe(this) { categoryList ->
            binding.categoryView.layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.categoryView.adapter = CategoryAdapter(categoryList.toMutableList())
            binding.progressBarCategory.visibility = View.GONE
        }
    }

    private fun observeBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.loadBanner().observe(this) { bannerList ->
            if (bannerList.isNotEmpty()) {
                Glide.with(this@MainActivity)
                    .load(bannerList[0].url)
                    .into(binding.banner)
            }
            binding.progressBarBanner.visibility = View.GONE
        }
    }
}
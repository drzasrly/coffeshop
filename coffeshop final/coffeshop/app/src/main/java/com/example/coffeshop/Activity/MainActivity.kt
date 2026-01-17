package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.CategoryAdapter
import com.example.coffeshop.Adapter.PopularAdapter
import com.example.coffeshop.Adapter.PromoAdapter
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

        // Cek Login
        if (TokenManager.instance.getToken() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        displayUserName()
        initPromo()
        initCategory()
        initPopular()
        initBottomMenu()
    }

    private fun displayUserName() {
        val userName = TokenManager.instance.getUserName()
        binding.textView2.text = if (!userName.isNullOrEmpty()) userName else "Guest"
    }

    private fun initPromo() {
        viewModel.loadBanner().observe(this) { bannerList ->
            if (!bannerList.isNullOrEmpty()) {
                val promoUrls = bannerList.map { it.url }
                binding.recyclerViewUpdate.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                    adapter = PromoAdapter(promoUrls)
                }
            }
        }
    }

    private fun initCategory() {
        viewModel.loadCategory().observe(this) { categoryList ->
            binding.categoryView.apply {
                layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = CategoryAdapter(categoryList.toMutableList())
            }
        }
    }

    private fun initPopular() {
        viewModel.loadPopular().observe(this) { popularList ->
            binding.recyclerView2.apply {
                layoutManager = GridLayoutManager(this@MainActivity, 2)
                adapter = PopularAdapter(popularList)
            }
        }
    }

    private fun initBottomMenu() {
        binding.explorerBtn.setOnClickListener {
            // Refresh halaman atau ke home
        }
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.wishlistBtn.setOnClickListener {
            // Pastikan nama activity-nya benar (wishlistActivity atau WishlistActivity)
            startActivity(Intent(this, wishlistActivity::class.java))
        }
        binding.pesananBtn.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
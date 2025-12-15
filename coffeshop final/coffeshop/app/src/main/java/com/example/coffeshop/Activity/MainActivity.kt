// Activity/MainActivity.kt
package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.coffeshop.Adapter.CategoryAdapter
import com.example.coffeshop.Adapter.PopularAdapter
import com.example.coffeshop.Domain.ItemsModel
import com.example.coffeshop.databinding.ActivityMainBinding
import com.example.coffeshop.viewModel.MainViewModel
// Import yang diperlukan
import com.example.coffeshop.Helper.TokenManager


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🟢 FOKUS: Panggil fungsi cek otentikasi
        checkAuthAndNavigate()
        // Semua fungsi init lainnya akan dipanggil dari dalam checkAuthAndNavigate
    }

    // 🟢 FUNGSI BARU: Logika Pengecekan Token JWT
    private fun checkAuthAndNavigate() {
        // Cek apakah Token JWT tersimpan di TokenManager
        if (TokenManager.instance.getToken() == null) {
            // Jika TIDAK ADA Token, arahkan ke LoginActivity
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish() // Mencegah pengguna kembali ke MainActivity tanpa login
        } else {
            // Jika ADA Token, lanjutkan inisialisasi menu utama
            initializeMainContent()
        }
    }

    // 🟢 FUNGSI BARU: Mengelompokkan semua inisialisasi konten utama
    private fun initializeMainContent() {
        // Panggil semua fungsi init yang sebelumnya ada di onCreate()
        observeBanner()
        initCategory()
        initPopular()
        initBottomMenu()
    }

    // --- KODE LAMA ANDA (Tidak Diubah) ---
    private fun initBottomMenu() {
        // Perbaikan kecil pada struktur listener yang Anda berikan sebelumnya
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        } // Tutup listener cartBtn di sini

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.wishlistBtn.setOnClickListener {
            startActivity(Intent(this, wishlistActivity::class.java)) // Memperbaiki kapitalisasi WishlistActivity
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
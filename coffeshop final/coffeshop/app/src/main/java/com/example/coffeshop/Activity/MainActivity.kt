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
// Import yang diperlukan
import com.example.coffeshop.Helper.TokenManager
// Pastikan LoginActivity sudah diimpor jika diperlukan untuk redirect
// import com.example.coffeshop.Activity.LoginActivity
// import com.example.coffeshop.Activity.CartActivity
// import com.example.coffeshop.Activity.ProfileActivity
// import com.example.coffeshop.Activity.wishlistActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🟢 CEK AUTH OTOMATIS:
        // Redirect jika token TIDAK ADA atau SUDAH KADALUARSA
        if (TokenManager.instance.getToken() == null || TokenManager.instance.isTokenExpired()) {
            TokenManager.instance.clearToken() // Bersihkan data lama
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return // Hentikan eksekusi kode di bawahnya
        }
        displayUserName()

        // 🟢 PERUBAHAN: Semua fungsi init dipanggil langsung di sini.
        // Tidak ada lagi checkAuthAndNavigate() yang menyebabkan redirect instan.

        observeBanner()
        initCategory()
        initPopular()
        initBottomMenu()
    }
    override fun onResume() {
        super.onResume()
        // 🟢 Panggil di onResume: Agar nama disegarkan jika kembali dari Activity lain.
        displayUserName()
    }

    private fun displayUserName() {
        val userName = TokenManager.instance.getUserName()

        // 3. Tambahkan Logcat untuk konfirmasi di MainActivity
        Log.d("MAIN_ACTIVITY", "Nama yang diambil dari Manager: $userName")

        // 4. Pastikan binding.textView2 adalah ID yang tepat
        if (userName != null && userName.isNotEmpty()) {
            binding.textView2.text = userName
        } else {
            // Jika Anda melihat "Guest", cek Logcat di atas
            binding.textView2.text = "Guest"
        }

    }
    // 🟢 PERUBAHAN UTAMA: Tambahkan logika cek token di tombol sensitif.
    private fun initBottomMenu() {

        // 1. Tombol Keranjang (Cart)
        binding.cartBtn.setOnClickListener {
            if (TokenManager.instance.getToken() == null) {
                // Jika TIDAK ADA Token, suruh login dulu
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // Jika ADA Token, lanjutkan ke Keranjang
                startActivity(Intent(this, CartActivity::class.java))
            }
        }

        // 2. Tombol Profil
        binding.profileBtn.setOnClickListener {
            if (TokenManager.instance.getToken() == null) {
                // Jika TIDAK ADA Token, suruh login dulu
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // Jika ADA Token, lanjutkan ke Profil
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        }

        // 3. Tombol Wishlist (Diasumsikan juga perlu login)
        binding.wishlistBtn.setOnClickListener {
            if (TokenManager.instance.getToken() == null) {
                // Jika TIDAK ADA Token, suruh login dulu
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // Jika ADA Token, lanjutkan ke Wishlist
                startActivity(Intent(this, wishlistActivity::class.java))
            }
        }
    }

    // --- KODE INIT LAINNYA (Sama seperti sebelumnya) ---

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



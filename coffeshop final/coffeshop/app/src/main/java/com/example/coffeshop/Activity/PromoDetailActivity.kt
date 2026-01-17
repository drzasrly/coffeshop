package com.example.coffeshop.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeshop.databinding.ActivityPromoDetailBinding

class PromoDetailActivity : AppCompatActivity() {

    // View Binding untuk mengakses komponen di XML
    private lateinit var binding: ActivityPromoDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi Binding
        binding = ActivityPromoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Logika Tombol Kembali
        binding.backBtn.setOnClickListener {
            // Menutup halaman ini dan kembali ke halaman sebelumnya
            onBackPressedDispatcher.onBackPressed()
        }

        // 3. Contoh mengambil data dari Intent (jika ada)
        val judulPromo = intent.getStringExtra("PROMO_TITLE")
        if (judulPromo != null) {
            binding.titleTv.text = judulPromo
        }
    }
}
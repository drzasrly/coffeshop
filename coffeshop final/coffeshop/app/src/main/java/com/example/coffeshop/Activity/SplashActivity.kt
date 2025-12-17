// File: Activity/SplashActivity.kt
package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast // Pastikan import Toast ada
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeshop.databinding.ActivitySplashBinding
// Import untuk mengakses token
import com.example.coffeshop.Helper.TokenManager

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            binding = ActivitySplashBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } catch (e: Exception) {
            // Jika crash terjadi saat binding/setContentView (layar hitam instan)
            Toast.makeText(this, "ERROR BINDING SPLASH: " + e.message, Toast.LENGTH_LONG).show()
            return
        }


        binding.startBtn.setOnClickListener {

            try {
                // 🟢 TITIK KRITIS UTAMA: Cek Token sebelum menavigasi
                val token = TokenManager.instance.getToken()

                if (token == null) {
                    // TIDAK ADA Token: Arahkan ke Halaman Login
                    startActivity(Intent(this, LoginActivity::class.java))
                    Toast.makeText(this, "Navigasi ke Login", Toast.LENGTH_SHORT).show()
                } else {
                    // ADA Token: Arahkan ke Halaman Utama (MainActivity)
                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "Navigasi ke Main", Toast.LENGTH_SHORT).show()
                }

                // Panggil finish() untuk menghapus SplashActivity dari stack
                finish()

            } catch (e: Exception) {
                // 🛑 Jika terjadi crash saat mengakses TokenManager atau saat startActivity() dipanggil

                // Tampilkan pesan error. Ini harus terlihat sebelum aplikasi close.
                Toast.makeText(this, "CRASH NAVIGASI/TOKEN: " + e.message, Toast.LENGTH_LONG).show()

                // Log error ke Logcat (walaupun sulit dicari, ini tetap harus dilakukan)
                e.printStackTrace()
            }
        }
    }
}
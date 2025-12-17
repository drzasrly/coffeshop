package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    // Menggunakan lateinit var untuk View Binding
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi View Binding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Konfigurasi UI (Edge to Edge dan Window Insets)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Panggil fungsi inisialisasi konten
        setupProfileUI()
        initButtons()
    }

    private fun setupProfileUI() {
        // Ambil data dari TokenManager yang disimpan saat login Laravel
        val userName = TokenManager.instance?.getUserName()
        val userEmail = TokenManager.instance?.getUserEmail()

        // Tampilkan Nama ke TextView
        binding.textView.text = if (!userName.isNullOrEmpty()) userName else "Guest"

        // Tampilkan Email ke TextView2
        binding.textView2.text = if (!userEmail.isNullOrEmpty()) userEmail else "Email Tidak Ditemukan"
    }

    private fun initButtons() {
        // Tombol Kembali
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 🟢 TAMBAHKAN: Logika Tombol Logout (Jika ada di layout Anda)
        // Jika ID tombol logout Anda berbeda, silakan sesuaikan (misal: binding.logoutBtn)
//        binding.logoutBtn.setOnClickListener {
//            // Hapus token dan session di SharedPreferences
//            TokenManager.instance?.clearToken()
//
//            // Redirect ke LoginActivity dan hapus tumpukan activity
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            finish()
//        }
    }
}
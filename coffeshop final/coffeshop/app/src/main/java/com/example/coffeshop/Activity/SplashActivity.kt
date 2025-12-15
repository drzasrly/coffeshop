package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeshop.databinding.ActivitySplashBinding
// Pastikan Anda mengimpor MainActivity yang benar
import com.example.coffeshop.Activity.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            // 1. Mulai MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            // 2. 🛑 PENTING: Panggil finish() untuk menghapus SplashActivity dari stack
            finish()
        }
    }
}
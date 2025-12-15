// Activity/LoginActivity.kt
package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.coffeshop.databinding.ActivityLoginBinding // Ganti sesuai nama package Binding Anda
import com.example.coffeshop.viewModel.LoginViewModel
import com.example.coffeshop.Helper.TokenManager // Untuk cek apakah user sudah login

class LoginActivity : AppCompatActivity() {

    // Deklarasi Binding (Wajib jika menggunakan View Binding)
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek apakah user sudah punya token
        if (TokenManager.instance.getToken() != null) {
            navigateToMain()
            return
        }

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil fungsi login di ViewModel
            viewModel.login(email, password)
        }

        // Optional: Tambahkan listener untuk navigasi ke halaman register
        // binding.tvRegister.setOnClickListener { /* Intent ke RegisterActivity */ }
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                "SUCCESS" -> {
                    Toast.makeText(this, "Login Berhasil! Token Disimpan.", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                else -> {
                    // Menampilkan pesan error dari Laravel atau Jaringan
                    Toast.makeText(this, "Gagal: $result", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToMain() {
        // Navigasi ke halaman utama (MainActivity)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Hapus Activity Login dari stack
    }
}
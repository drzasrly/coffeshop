package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.coffeshop.databinding.ActivityLoginBinding
import com.example.coffeshop.viewModel.LoginViewModel
import com.example.coffeshop.Helper.TokenManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi TokenManager terlebih dahulu
        TokenManager.initialize(applicationContext)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Cek apakah user sudah login dan token belum expired
        if (TokenManager.instance.getToken() != null && !TokenManager.instance.isTokenExpired()) {
            navigateToMain()
            return
        }

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            if (result == "SUCCESS") {
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                // Menampilkan pesan error dari server atau exception
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        // Gunakan flags agar user tidak bisa kembali ke halaman login setelah masuk ke Main
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
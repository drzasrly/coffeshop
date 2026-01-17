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

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek Session
        if (TokenManager.instance.getToken() != null) {
            navigateToMain()
            return
        }

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        // Tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.login(email, password)
        }

        // Navigasi ke Halaman Register
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                "SUCCESS" -> {
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                else -> {
                    Toast.makeText(this, "Gagal: $result", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
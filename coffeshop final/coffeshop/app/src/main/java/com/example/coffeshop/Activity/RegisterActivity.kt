package com.example.coffeshop.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coffeshop.Domain.RegisterRequest
import com.example.coffeshop.Repository.RetrofitClient
import com.example.coffeshop.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi input di sisi Android
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                Toast.makeText(this, "Password minimal 8 karakter", Toast.LENGTH_SHORT).show()
            } else {
                performRegister(name, email, password)
            }
        }

        binding.tvLoginLink.setOnClickListener {
            finish() // Kembali ke halaman Login
        }
    }

    private fun performRegister(name: String, email: String, pass: String) {
        lifecycleScope.launch {
            try {
                // Membuat objek request dengan password_confirmation
                // Kita kirim variabel 'pass' yang sama ke kedua field
                val request = RegisterRequest(
                    name = name,
                    email = email,
                    password = pass,
                    password_confirmation = pass
                )

                // Memanggil RetrofitClient.instance (Ganti .apiService ke .instance jika perlu)
                val response = RetrofitClient.instance.registerUser(request)

                if (response.success) {
                    Toast.makeText(this@RegisterActivity, "Registrasi Berhasil!", Toast.LENGTH_LONG).show()
                    finish() // Tutup halaman dan balik ke Login
                } else {
                    // Menampilkan pesan error dari Laravel (misal: email sudah terdaftar)
                    Toast.makeText(this@RegisterActivity, response.message, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("REGISTER_ERROR", "Error: ${e.message}")
                Toast.makeText(this@RegisterActivity, "Koneksi Error: Periksa IP Laragon/Internet", Toast.LENGTH_LONG).show()
            }
        }
    }
}
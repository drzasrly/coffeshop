package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.Repository.RetrofitClient
import com.example.coffeshop.databinding.ActivityChangePasswordBinding
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol Back
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnUpdatePassword.setOnClickListener {
            val oldPass = binding.etCurrentPassword.text.toString().trim()
            val newPass = binding.etNewPassword.text.toString().trim()
            val confirmPass = binding.etConfirmPassword.text.toString().trim()
            val token = TokenManager.instance.getToken()

            // Validasi lokal
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass.length < 8) {
                Toast.makeText(this, "Password baru minimal 8 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(this, "Konfirmasi password baru tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (token != null) {
                updatePasswordToServer("Bearer $token", oldPass, newPass, confirmPass)
            } else {
                Toast.makeText(this, "Sesi berakhir, silakan login lagi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePasswordToServer(token: String, oldPass: String, newPass: String, confirmPass: String) {
        lifecycleScope.launch {
            try {
                binding.btnUpdatePassword.isEnabled = false

                // PERBAIKAN: Menggunakan .instance (bukan .apiService)
                val response = RetrofitClient.instance.changePassword(token, oldPass, newPass, confirmPass)

                if (response.isSuccessful) {
                    Toast.makeText(this@ChangePasswordActivity, "Password Berhasil Diubah!", Toast.LENGTH_LONG).show()

                    // Clear token agar user login ulang dengan password baru
                    TokenManager.instance.clearToken()
                    val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    binding.btnUpdatePassword.isEnabled = true
                    val errorDetail = response.errorBody()?.string()
                    Log.e("CHG_PASS_DEBUG", "Server Error: $errorDetail")
                    Toast.makeText(this@ChangePasswordActivity, "Gagal: Password lama salah atau validasi gagal", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                binding.btnUpdatePassword.isEnabled = true
                Log.e("CHG_PASS_DEBUG", "Exception: ${e.message}")
                Toast.makeText(this@ChangePasswordActivity, "Error Koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
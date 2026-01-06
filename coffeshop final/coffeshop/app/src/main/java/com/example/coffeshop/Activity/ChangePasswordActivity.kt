package com.example.coffeshop.Activity

import android.os.Bundle
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

        binding.backBtn.setOnClickListener { finish() }

        binding.btnUpdatePassword.setOnClickListener {
            // PENTING: Gunakan .trim() untuk membersihkan spasi kosong
            val oldPass = binding.etCurrentPassword.text.toString().trim()
            val newPass = binding.etNewPassword.text.toString().trim()
            val confirmPass = binding.etConfirmPassword.text.toString().trim()
            val token = TokenManager.instance?.getToken()

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(this, "Konfirmasi password baru tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (token != null) {
                updatePasswordToServer("Bearer $token", oldPass, newPass)
            }
        }
    }

    private fun updatePasswordToServer(token: String, oldPass: String, newPass: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.changePassword(token, oldPass, newPass)

                if (response.isSuccessful) {
                    Toast.makeText(this@ChangePasswordActivity, "Password Berhasil Diubah!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // Cek pesan error dari server jika gagal
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@ChangePasswordActivity, "Gagal: Password lama salah atau sesi habis", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ChangePasswordActivity, "Error Koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
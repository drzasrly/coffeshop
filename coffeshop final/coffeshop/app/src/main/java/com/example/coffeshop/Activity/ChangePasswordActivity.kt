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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            TokenManager.initialize(applicationContext)
        } catch (e: Exception) {
            Log.e("DEBUG_APP", "TokenManager error: ${e.message}")
        }

        binding.backBtn.setOnClickListener { finish() }

        binding.btnUpdatePassword.setOnClickListener {
            val oldPass = binding.etCurrentPassword.text.toString().trim()
            val newPass = binding.etNewPassword.text.toString().trim()
            val confirmPass = binding.etConfirmPassword.text.toString().trim()

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(this, "Konfirmasi password baru tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updatePassword(oldPass, newPass, confirmPass)
        }
    }

    private fun updatePassword(old: String, new: String, confirm: String) {
        lifecycleScope.launch {
            try {
                binding.btnUpdatePassword.isEnabled = false
                binding.btnUpdatePassword.text = "Updating..."

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.changePassword(old, new, confirm)
                }

                if (response.isSuccessful) {
                    Toast.makeText(this@ChangePasswordActivity, "Berhasil! Silakan Login Ulang", Toast.LENGTH_LONG).show()

                    TokenManager.instance.clearToken()

                    val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    resetButton()
                    Toast.makeText(this@ChangePasswordActivity, "Gagal: Password lama salah!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                resetButton()
                Toast.makeText(this@ChangePasswordActivity, "Koneksi Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetButton() {
        binding.btnUpdatePassword.isEnabled = true
        binding.btnUpdatePassword.text = "Update Password"
    }
}

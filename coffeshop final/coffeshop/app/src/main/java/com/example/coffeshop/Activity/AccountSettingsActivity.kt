package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeshop.databinding.ActivityAccountSettingsBinding

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi View Binding
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Semua listener tombol harus berada di DALAM kurung kurawal onCreate

        // 2. Tombol Back
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 3. Klik Change Password
        // Di dalam onCreate AccountSettingsActivity
        binding.btnChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        // 4. Klik Delete Account
        binding.btnDeleteAccount.setOnClickListener {
            Toast.makeText(this, "Please contact support to delete account", Toast.LENGTH_SHORT).show()
        }

        // 5. Klik Privacy Policy (Opsional)
        binding.btnPrivacyPolicy.setOnClickListener {
            Toast.makeText(this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show()
        }
    } // Kurung kurawal penutup onCreate
} // Kurung kurawal penutup Class
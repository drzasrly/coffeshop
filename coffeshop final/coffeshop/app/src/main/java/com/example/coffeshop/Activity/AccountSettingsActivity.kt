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

        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Tombol Back
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 2. Klik Change Password
        binding.btnChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        // 3. Klik Privacy Policy - SEKARANG BERFUNGSI
        binding.btnPrivacyPolicy.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        // 4. Klik Delete Account
        binding.btnDeleteAccount.setOnClickListener {
            Toast.makeText(this, "Please contact support to delete account", Toast.LENGTH_SHORT).show()
        }
    }
}
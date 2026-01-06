package com.example.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupProfileUI()
        initButtons()
    }

    private fun setupProfileUI() {
        val userName = TokenManager.instance?.getUserName()
        val userEmail = TokenManager.instance?.getUserEmail()

        binding.textView.text = if (!userName.isNullOrEmpty()) userName else "Guest"
        binding.textView2.text = if (!userEmail.isNullOrEmpty()) userEmail else "Email Tidak Ditemukan"
    }

    private fun initButtons() {
        binding.apply {
            // Tombol Kembali
            backBtn.setOnClickListener {
                finish()
            }

            // Tombol Account Settings (Di XML kamu ID-nya adalah 'button')
            button.setOnClickListener {
                val intent = Intent(this@ProfileActivity, AccountSettingsActivity::class.java)
                startActivity(intent)
            }

            // Tombol Personal Information (Di XML kamu ID-nya adalah 'button3')
            button3.setOnClickListener {
                val intent = Intent(this@ProfileActivity, PersonalInformationActivity::class.java)
                startActivity(intent)
            }

            // Tombol Logout
            btnLogout.setOnClickListener {
                performLogout()
            }
        }
    }

    private fun performLogout() {
        TokenManager.instance?.clearToken()
        Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Update tampilan nama & email setiap kali user kembali ke halaman ini
        val userName = TokenManager.instance?.getUserName()
        binding.textView.text = if (!userName.isNullOrEmpty()) userName else "Guest"
    }
}
package com.example.coffeshop.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.databinding.ActivityPersonalInformationBinding

class PersonalInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Load data lama dari TokenManager
        val currentName = TokenManager.instance?.getUserName()
        val currentEmail = TokenManager.instance?.getUserEmail()

        binding.etName.setText(currentName)
        binding.etEmail.setText(currentEmail)

        // 2. Tombol Kembali (Pasti berfungsi dengan finish())
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 3. Tombol Save Changes
        binding.btnSave.setOnClickListener {
            val newName = binding.etName.text.toString().trim()

            if (newName.isNotEmpty()) {
                // UPDATE LOKAL: Agar saat kembali ke Profile, namanya sudah berubah
                // Pastikan di TokenManager kamu ada fungsi saveUserName atau setUserName
                TokenManager.instance?.saveUserName(newName)

                Toast.makeText(this, "Nama berhasil diperbarui!", Toast.LENGTH_SHORT).show()

                // Tutup activity dan kembali ke Profile
                finish()
            } else {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
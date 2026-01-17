package com.example.coffeshop.Activity

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeshop.R
import com.example.coffeshop.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi Binding
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Tombol Back
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 3. Panggil fungsi untuk mengisi teks
        setupPrivacyText()
    }

    private fun setupPrivacyText() {
        // Mengambil teks dari strings.xml
        val policyText = getString(R.string.privacy_policy_content)

        // Memasukkan teks ke TextView tvPrivacyContent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvPrivacyContent.text = Html.fromHtml(policyText, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            binding.tvPrivacyContent.text = Html.fromHtml(policyText)
        }
    }
}
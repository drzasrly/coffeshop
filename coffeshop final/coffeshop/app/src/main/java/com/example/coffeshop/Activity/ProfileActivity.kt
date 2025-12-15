package com.example.coffeshop.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffeshop.R
import com.example.coffeshop.databinding.ActivityProfileBinding


class ProfileActivity : AppCompatActivity() {

    // Deklarasi binding. Variabel ini akan menyimpan referensi ke layout.
    lateinit var binding: ActivityProfileBinding

    private var tax: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- LANGKAH PENTING: INISIALISASI BINDING HARUS DI AWAL onCreate ---

        // 1. Inisialisasi properti 'binding' di sini.
        // Ini adalah langkah yang hilang/salah tempat di kode Anda sebelumnya.
        binding = ActivityProfileBinding.inflate(layoutInflater)

        // 2. Terapkan View Binding ke ContentView.
        setContentView(binding.root)

        // 3. Panggil enableEdgeToEdge setelah setContentView.
        enableEdgeToEdge()

        // 4. Panggil fungsi yang menggunakan 'binding' setelah diinisialisasi,
        // sekarang ini aman untuk dilakukan.
        initCartList()

        // Menangani system bars (biasanya boilerplate dari template Android)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initCartList() {
        // Karena 'binding' sudah diinisialisasi di onCreate(),
        // kode ini tidak akan menyebabkan UninitializedPropertyAccessException.
        binding.backBtn.setOnClickListener {
            finish()
        }

        // TODO: Tambahkan kode inisialisasi RecyclerView, Adapter, dll., di sini.
    }
}
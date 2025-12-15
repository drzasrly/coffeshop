package com.example.coffeshop.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.coffeshop.Domain.ItemsModel
import com.example.coffeshop.Helper.ChangeNumberItemsListener
import com.example.coffeshop.Helper.ManagmentCart
import com.example.coffeshop.R // Wajib
import com.example.coffeshop.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        bundle()
        initSizeList()
    }

    private fun initSizeList() {
        // ... (Logika ukuran tombol)
    }

    /**
     * Memperbarui ikon Wishlist (TERISI/KOSONG) berdasarkan status item.
     * Mengatasi masalah tint dengan membersihkan ColorFilter.
     */
    private fun updateFavoriteButton() {
        val isFav = managmentCart.isItemInWishlist(item)
        if (isFav) {
            // Status: TERISI
            // Menggunakan ikon solid yang sudah Anda buat.
            binding.wishlistBtn.setImageResource(R.drawable.ic_favorite_filled)

            // PENTING: Hapus filter warna agar warna solid terlihat
            binding.wishlistBtn.clearColorFilter()
        } else {
            // Status: KOSONG
            // Menggunakan ikon garis luar (btn_3).
            binding.wishlistBtn.setImageResource(R.drawable.btn_3)

            // Terapkan filter warna agar garis luarnya berwarna cokelat.
            // ASUMSI: R.color.darkBrown ada
            binding.wishlistBtn.setColorFilter(
                ContextCompat.getColor(this, R.color.darkBrown)
            )
        }
    }


    private fun bundle() {
        binding.apply {
            backBtn.setOnClickListener {
                finish()
            }
            wishlistBtn.visibility = View.VISIBLE

            @Suppress("DEPRECATION")
            item = intent.getSerializableExtra("object") as ItemsModel

            if (item.numberInCart == 0) {
                item.numberInCart = 1
            }
            numberInCartTxt.text = item.numberInCart.toString()

            // 1. Inisialisasi tampilan favorit awal
            updateFavoriteButton() // Panggil di sini agar status awal dimuat

            // 2. Load gambar (Glide)
            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(picMain)

            // 3. Set data UI lainnya
            titleTxt.text = item.title
            descTxt.text = item.description
            priceTxt.text = "$" + item.price.toString()
            ratingTxt.text = item.rating.toString()

            // ... (Listener Cart dan lainnya)

            // 4. Logika Wishlist
            wishlistBtn.setOnClickListener {
                val isFav = managmentCart.isItemInWishlist(item)
                if (isFav) {
                    // Hapus Item
                    managmentCart.removeFromWishlist(item, object : ChangeNumberItemsListener{
                        override fun onChanged() {} // Listener dummy
                    })
                    Toast.makeText(this@DetailActivity, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                } else {
                    // Tambah Item
                    managmentCart.addToWishlist(item)
                    Toast.makeText(this@DetailActivity, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                }
                updateFavoriteButton() // PENTING: Perbarui tampilan tombol setelah aksi
            }
        }
    }
}
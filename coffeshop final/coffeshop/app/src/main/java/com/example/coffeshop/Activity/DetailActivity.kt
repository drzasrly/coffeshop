package com.example.coffeshop.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.coffeshop.Domain.ItemsModel
import com.example.coffeshop.Domain.WishlistRequest
import com.example.coffeshop.Domain.WishlistResponse
import com.example.coffeshop.Helper.ChangeNumberItemsListener
import com.example.coffeshop.Helper.ManagmentCart
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.R
import com.example.coffeshop.Repository.ApiService
import com.example.coffeshop.Repository.RetrofitClient
import com.example.coffeshop.databinding.ActivityDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var apiInterface: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        // Inisialisasi API Client
        apiInterface = RetrofitClient.apiService

        bundle()
        // initSizeList() // Aktifkan jika fungsi ini sudah ada kodenya
    }

    private fun updateFavoriteButton() {
        val isFav = managmentCart.isItemInWishlist(item)
        if (isFav) {
            binding.wishlistBtn.setImageResource(R.drawable.ic_favorite_filled)
            binding.wishlistBtn.clearColorFilter()
        } else {
            binding.wishlistBtn.setImageResource(R.drawable.btn_3)
            binding.wishlistBtn.setColorFilter(
                ContextCompat.getColor(this, R.color.darkBrown)
            )
        }
    }

    private fun bundle() {
        binding.apply {
            backBtn.setOnClickListener { finish() }
            wishlistBtn.visibility = View.VISIBLE

            @Suppress("DEPRECATION")
            item = intent.getSerializableExtra("object") as ItemsModel

            if (item.numberInCart == 0) { item.numberInCart = 1 }
            numberInCartTxt.text = item.numberInCart.toString()

            updateFavoriteButton()

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(picMain)

            titleTxt.text = item.title
            descTxt.text = item.description
            priceTxt.text = "$" + item.price.toString()
            ratingTxt.text = item.rating.toString()

            // LOGIKA KLIK WISHLIST
            wishlistBtn.setOnClickListener {
                val isFav = managmentCart.isItemInWishlist(item)
                if (isFav) {
                    // 1. Hapus Lokal
                    managmentCart.removeFromWishlist(item, object : ChangeNumberItemsListener {
                        override fun onChanged() {}
                    })
                    Toast.makeText(this@DetailActivity, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                } else {
                    // 1. Tambah Lokal (Supaya Ikon Cepat Berubah)
                    managmentCart.addToWishlist(item)

                    // 2. Tambah ke Laravel MySQL
                    tambahKeWishlistLaravel(item.id.toString())

                    Toast.makeText(this@DetailActivity, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                }
                updateFavoriteButton()
            }
        }
    }

    private fun tambahKeWishlistLaravel(menuId: String) {
        // Ambil token dari TokenManager
        val token = TokenManager.instance.getToken()

        if (token == null) {
            Log.e("API_DEBUG", "Gagal: User belum login / Token tidak ditemukan")
            return
        }

        val tokenHeader = "Bearer $token"
        val request = WishlistRequest(menuId)

        apiInterface.addWishlist(tokenHeader, request).enqueue(object : Callback<WishlistResponse> {
            override fun onResponse(call: Call<WishlistResponse>, response: Response<WishlistResponse>) {
                if (response.isSuccessful) {
                    Log.d("API_DEBUG", "Berhasil simpan ke MySQL!")
                } else {
                    // Jika error 401 atau 500 akan tercetak di sini
                    Log.e("API_DEBUG", "Gagal! Kode: ${response.code()} Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                Log.e("API_DEBUG", "Koneksi ke Server Gagal: ${t.message}")
            }
        })
    }
}
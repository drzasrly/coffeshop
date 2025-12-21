package com.example.coffeshop.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
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
import com.example.coffeshop.viewModel.WishlistViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var apiInterface: ApiService
    private lateinit var viewModel: WishlistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        apiInterface = RetrofitClient.apiService

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(WishlistViewModel::class.java)

        bundle()
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

            val receivedItem = intent.getSerializableExtra("object") as? ItemsModel
            if (receivedItem != null) {
                item = receivedItem
            } else {
                finish()
                return
            }

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
            // LOGIKA KLIK WISHLIST
            wishlistBtn.setOnClickListener {
                // Pengecekan status awal sebelum aksi
                val isCurrentlyFav = managmentCart.isItemInWishlist(item)
                val currentMenuId = item.id.toString()

                if (isCurrentlyFav) {
                    // HAPUS: Panggil ViewModel dulu agar data ID konsisten
                    viewModel.removeFromWishlist(currentMenuId)

                    // Baru hapus dari ManagementCart (SharedPreferences/UI Helper)
                    managmentCart.removeFromWishlist(item, object : ChangeNumberItemsListener {
                        override fun onChanged() {
                            // Opsional: jalankan kode jika UI butuh update setelah data benar-benar hilang
                        }
                    })

                    Toast.makeText(this@DetailActivity, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                } else {
                    // TAMBAH: Tambah ke ManagementCart dulu
                    managmentCart.addToWishlist(item)

                    // Kirim ke Laravel & Room via ViewModel
                    viewModel.addProductToWishlist(
                        menuId = currentMenuId,
                        name = item.title,
                        price = item.price.toString(),
                        imageUrl = item.picUrl[0]
                    )

                    Toast.makeText(this@DetailActivity, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                }

                // Refresh status icon
                updateFavoriteButton()

                // Tambahkan Log untuk verifikasi di Logcat
                Log.d("API_DEBUG", "Klik Tombol Wishlist - MenuID: $currentMenuId, Action: ${if (isCurrentlyFav) "DELETE" else "ADD"}")
            }
        }
    }
}
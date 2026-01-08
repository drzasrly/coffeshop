package com.example.coffeshop.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.coffeshop.Domain.ItemsModel
import com.example.coffeshop.Helper.ChangeNumberItemsListener
import com.example.coffeshop.Helper.ManagmentCart
import com.example.coffeshop.R
import com.example.coffeshop.Repository.ApiService
import com.example.coffeshop.Repository.RetrofitClient
import com.example.coffeshop.databinding.ActivityDetailBinding
import com.example.coffeshop.viewModel.WishlistViewModel
import com.example.coffeshop.viewModel.CartViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var apiInterface: ApiService
    private lateinit var viewModelWishlist: WishlistViewModel
    private lateinit var viewModelCart: CartViewModel

    // Variabel lokal untuk menampung jumlah yang akan dibeli
    private var numberOrder = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        apiInterface = RetrofitClient.apiService

        // Inisialisasi ViewModels
        viewModelWishlist = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(WishlistViewModel::class.java)

        viewModelCart = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CartViewModel::class.java)
        setupSizeSelection()
        getBundle()
    }

    private fun getBundle() {
        binding.apply {
            // Tombol Kembali
            backBtn.setOnClickListener { finish() }

            // Ambil data dari Intent
            val receivedItem = intent.getSerializableExtra("object") as? ItemsModel
            if (receivedItem != null) {
                item = receivedItem
            } else {
                finish()
                return
            }

            // Set Data ke UI
            titleTxt.text = item.title
            descTxt.text = item.description
            priceTxt.text = "Rp" + item.price.toString()
            ratingTxt.text = item.rating.toString()
            numberInCartTxt.text = numberOrder.toString()

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(picMain)

            updateFavoriteButton()

            // --- LOGIKA TOMBOL QUANTITY (PLUS & MINUS) ---

            plusBtn.setOnClickListener {
                numberOrder++
                numberInCartTxt.text = numberOrder.toString()
            }

            minusBtn.setOnClickListener {
                if (numberOrder > 1) {
                    numberOrder--
                    numberInCartTxt.text = numberOrder.toString()
                }
            }

            // --- LOGIKA ADD TO CART ---

            // Di dalam fungsi bundle() atau getBundle()
            addToCartBtn.setOnClickListener {
                val qty = numberInCartTxt.text.toString().toInt()

                // Panggil ViewModel dengan tambahan parameter selectedSize
                viewModelCart.addToCart(
                    menuId = item.id.toString(),
                    name = item.title,
                    price = item.price.toString(),
                    imageUrl = item.picUrl[0],
                    qty = qty,
                    size = selectedSize // <--- Tambahkan ini
                )

                Toast.makeText(this@DetailActivity, "Added to cart: $selectedSize", Toast.LENGTH_SHORT).show()
            }

            // --- LOGIKA WISHLIST ---

            wishlistBtn.setOnClickListener {
                val isCurrentlyFav = managmentCart.isItemInWishlist(item)
                val currentMenuId = item.id.toString()

                if (isCurrentlyFav) {
                    viewModelWishlist.removeFromWishlist(currentMenuId)
                    managmentCart.removeFromWishlist(item, object : ChangeNumberItemsListener {
                        override fun onChanged() {}
                    })
                    Toast.makeText(this@DetailActivity, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                } else {
                    managmentCart.addToWishlist(item)
                    viewModelWishlist.addProductToWishlist(
                        menuId = currentMenuId,
                        name = item.title,
                        price = item.price.toString(),
                        imageUrl = item.picUrl[0]
                    )
                    Toast.makeText(this@DetailActivity, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                }
                updateFavoriteButton()
            }
        }
    }

    // Di dalam class DetailActivity
    private var selectedSize: String = "Small" // Default selection

    private fun setupSizeSelection() {
        binding.apply {
            // Klik Small
            smallBtn.setOnClickListener {
                selectedSize = "Small"
                updateSizeUI("Small")
            }

            // Klik Medium
            mediumBtn.setOnClickListener {
                selectedSize = "Medium"
                updateSizeUI("Medium")
            }

            // Klik Large
            LargeBtn.setOnClickListener {
                selectedSize = "Large"
                updateSizeUI("Large")
            }
        }
    }

    private fun updateSizeUI(size: String) {
        binding.apply {
            // Reset warna semua tombol ke default (Dark Brown)
            smallBtn.setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.darkBrown))
            mediumBtn.setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.darkBrown))
            LargeBtn.setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.darkBrown))

            // Berikan tanda pada yang dipilih (Misal: ganti warna atau background)
            // Jika kamu punya drawable khusus untuk "selected", gunakan setBackgroundResource
            when (size) {
                "Small" -> smallBtn.setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.white))
                "Medium" -> mediumBtn.setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.white))
                "Large" -> LargeBtn.setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.white))
            }
        }
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
}
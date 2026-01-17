package com.example.coffeshop.Activity

import android.os.Bundle
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
import com.example.coffeshop.databinding.ActivityDetailBinding
import com.example.coffeshop.viewModel.WishlistViewModel
import com.example.coffeshop.viewModel.CartViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var viewModelWishlist: WishlistViewModel
    private lateinit var viewModelCart: CartViewModel

    private var numberOrder = 1
    private var selectedSize: String = "Small" // Default selection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        viewModelWishlist = ViewModelProvider(this)[WishlistViewModel::class.java]
        viewModelCart = ViewModelProvider(this)[CartViewModel::class.java]

        setupSizeSelection()
        getBundle()
    }

    private fun getBundle() {
        binding.apply {
            backBtn.setOnClickListener { finish() }

            // Ambil data dari Intent
            val receivedItem = intent.getSerializableExtra("object") as? ItemsModel
            if (receivedItem != null) {
                item = receivedItem
            } else {
                Toast.makeText(this@DetailActivity, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            // Set Data ke UI
            titleTxt.text = item.title
            descTxt.text = item.description
            priceTxt.text = "Rp ${item.price}"
            ratingTxt.text = item.rating.toString()
            numberInCartTxt.text = numberOrder.toString()

            // PERBAIKAN 1: Hapus [0] karena picUrl sekarang adalah String
            Glide.with(this@DetailActivity)
                .load(item.picUrl)
                .into(picMain)

            updateFavoriteButton()

            // LOGIKA TOMBOL QUANTITY
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

            // LOGIKA ADD TO CART
            addToCartBtn.setOnClickListener {
                // PERBAIKAN 2: Hapus [0] pada imageUrl
                viewModelCart.addToCart(
                    menuId = item.id.toString(),
                    name = item.title,
                    price = item.price.toString(),
                    imageUrl = item.picUrl,
                    qty = numberOrder,
                    size = selectedSize
                )

                Toast.makeText(this@DetailActivity, "Added to cart: $selectedSize", Toast.LENGTH_SHORT).show()
            }

            // LOGIKA WISHLIST
            wishlistBtn.setOnClickListener {
                val isCurrentlyFav = managmentCart.isItemInWishlist(item)
                val currentMenuId = item.id.toString()

                if (isCurrentlyFav) {
                    viewModelWishlist.removeFromWishlist(currentMenuId)
                    managmentCart.removeFromWishlist(item, object : ChangeNumberItemsListener {
                        override fun onChanged() {
                            updateFavoriteButton()
                        }
                    })
                    Toast.makeText(this@DetailActivity, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                } else {
                    managmentCart.addToWishlist(item)
                    // PERBAIKAN 3: Hapus [0] pada imageUrl
                    viewModelWishlist.addProductToWishlist(
                        menuId = currentMenuId,
                        name = item.title,
                        price = item.price.toString(),
                        imageUrl = item.picUrl
                    )
                    Toast.makeText(this@DetailActivity, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                }
                updateFavoriteButton()
            }
        }
    }

    private fun setupSizeSelection() {
        binding.apply {
            smallBtn.setOnClickListener {
                selectedSize = "Small"
                updateSizeUI("Small")
            }
            mediumBtn.setOnClickListener {
                selectedSize = "Medium"
                updateSizeUI("Medium")
            }
            LargeBtn.setOnClickListener {
                selectedSize = "Large"
                updateSizeUI("Large")
            }
        }
    }

    private fun updateSizeUI(size: String) {
        binding.apply {
            val darkBrown = ContextCompat.getColor(this@DetailActivity, R.color.darkBrown)
            val white = ContextCompat.getColor(this@DetailActivity, R.color.white)

            smallBtn.setTextColor(darkBrown)
            mediumBtn.setTextColor(darkBrown)
            LargeBtn.setTextColor(darkBrown)

            when (size) {
                "Small" -> smallBtn.setTextColor(white)
                "Medium" -> mediumBtn.setTextColor(white)
                "Large" -> LargeBtn.setTextColor(white)
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
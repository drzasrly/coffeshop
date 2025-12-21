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
import com.example.coffeshop.Helper.ChangeNumberItemsListener
import com.example.coffeshop.Helper.ManagmentCart
import com.example.coffeshop.R
import com.example.coffeshop.Repository.ApiService
import com.example.coffeshop.Repository.RetrofitClient
import com.example.coffeshop.databinding.ActivityDetailBinding
import com.example.coffeshop.viewModel.WishlistViewModel
import com.example.coffeshop.viewModel.CartViewModel // Pastikan di-import

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var apiInterface: ApiService
    private lateinit var viewModel: WishlistViewModel

    // 1. TAMBAHKAN deklarasi viewModelCart di sini agar tidak error
    private lateinit var viewModelCart: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        apiInterface = RetrofitClient.apiService

        // Inisialisasi Wishlist ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(WishlistViewModel::class.java)

        // 2. INISIALISASI CartViewModel
        viewModelCart = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CartViewModel::class.java)

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
            wishlistBtn.setOnClickListener {
                val isCurrentlyFav = managmentCart.isItemInWishlist(item)
                val currentMenuId = item.id.toString()

                if (isCurrentlyFav) {
                    viewModel.removeFromWishlist(currentMenuId)
                    managmentCart.removeFromWishlist(item, object : ChangeNumberItemsListener {
                        override fun onChanged() {}
                    })
                    Toast.makeText(this@DetailActivity, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                } else {
                    managmentCart.addToWishlist(item)
                    viewModel.addProductToWishlist(
                        menuId = currentMenuId,
                        name = item.title,
                        price = item.price.toString(),
                        imageUrl = item.picUrl[0]
                    )
                    Toast.makeText(this@DetailActivity, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                }
                updateFavoriteButton()
                Log.d("API_DEBUG", "Klik Tombol Wishlist - MenuID: $currentMenuId, Action: ${if (isCurrentlyFav) "DELETE" else "ADD"}")
            }

            // LOGIKA KLIK ADD TO CART
            addToCartBtn.setOnClickListener {
                val qty = numberInCartTxt.text.toString().toInt()

                // Sekarang viewModelCart sudah terdefinisi
                viewModelCart.addToCart(
                    menuId = item.id.toString(),
                    name = item.title,
                    price = item.price.toString(), // Pastikan parameter di ViewModel adalah String
                    imageUrl = item.picUrl[0],
                    qty = qty
                )

                // 3. Pastikan memanggil .show() pada Toast
                Toast.makeText(this@DetailActivity, "Ditambahkan ke Keranjang", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
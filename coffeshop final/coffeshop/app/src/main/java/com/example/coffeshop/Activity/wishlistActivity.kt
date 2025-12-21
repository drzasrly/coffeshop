package com.example.coffeshop.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.WishlistAdapter
import com.example.coffeshop.Adapter.WishlistChangeListener
import com.example.coffeshop.Domain.ItemsModel
import com.example.coffeshop.Helper.ManagmentCart
import com.example.coffeshop.databinding.ActivityWishlistBinding
import com.example.coffeshop.viewModel.WishlistViewModel // Tambahkan Import

class wishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding
    private lateinit var managmentCart: ManagmentCart
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var itemsList: ArrayList<ItemsModel>
    private lateinit var viewModel: WishlistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        // 1. Inisialisasi ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(WishlistViewModel::class.java)

        setupHeader()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    private fun setupHeader() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        itemsList = managmentCart.getWishlist()

        if (itemsList.isEmpty()) {
            binding.emptyWishlistText.visibility = View.VISIBLE
            binding.wishlistRecyclerView.visibility = View.GONE
        } else {
            binding.emptyWishlistText.visibility = View.GONE
            binding.wishlistRecyclerView.visibility = View.VISIBLE

            // 2. Kirim 'viewModel' ke dalam Constructor Adapter
            wishlistAdapter = WishlistAdapter(itemsList, this, viewModel)

            binding.wishlistRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@wishlistActivity)
                adapter = wishlistAdapter
            }

            wishlistAdapter.wishlistChangeListener = object : WishlistChangeListener {
                override fun onWishlistChanged() {
                    updateWishlistUI()
                }
            }
        }
    }

    private fun updateWishlistUI() {
        if (itemsList.isEmpty()) {
            binding.emptyWishlistText.visibility = View.VISIBLE
            binding.wishlistRecyclerView.visibility = View.GONE
        } else {
            binding.emptyWishlistText.visibility = View.GONE
            binding.wishlistRecyclerView.visibility = View.VISIBLE
        }
    }
}
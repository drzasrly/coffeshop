// viewModel/WishlistViewModel.kt
package com.example.coffeshop.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeshop.Domain.WishlistRequest
import com.example.coffeshop.Repository.RetrofitClient
import kotlinx.coroutines.launch

class WishlistViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService
    // private val firebaseRepo = FirebaseRepository() // Opsional, jika mau ambil detail menu di sini

    // ... LiveData untuk Wishlist dan error

    fun loadWishlist() {
        viewModelScope.launch {
            try {
                val userWishlist = apiService.getWishlistItems()
                // Update LiveData
            } catch (e: Exception) {
                // Tangani 401 Unauthorized (Token expired/salah) atau error koneksi
            }
        }
    }

    fun addToWishlist(menuId: String) {
        viewModelScope.launch {
            try {
                val request = WishlistRequest(menuId)
                val response = apiService.addToWishlist(request)

                if (response.success) {
                    loadWishlist()
                }
            } catch (e: Exception) {
                // Tangani error, misal: item sudah ada (422) atau jaringan
            }
        }
    }
}
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

    // Di dalam Activity atau Adapter saat tombol diklik
    fun addProductToWishlist(productId: String) {
        viewModelScope.launch {
            try {
                // Mengirim ID Produk (dari Firebase) ke SQL (Laravel)
                val response = RetrofitClient.apiService.addToWishlist(WishlistRequest(productId))
                if (response.success) {
                    // Berhasil tersimpan di SQL
                }
            } catch (e: Exception) {
                // Tangani error
            }

        }
    }
}
package com.example.coffeshop.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.coffeshop.AppDatabase
import com.example.coffeshop.Domain.WishlistModel
import com.example.coffeshop.Domain.WishlistRequest
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.Repository.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WishlistViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.apiService
    private val wishlistDao = AppDatabase.getDatabase(application).wishlistDao()

    val localWishlist = wishlistDao.getAllWishlist().asLiveData()

    // Di dalam WishlistViewModel.kt
    fun addProductToWishlist(menuId: String, name: String, price: String, imageUrl: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = TokenManager.instance.getToken()
                val response = apiService.addWishlist("Bearer $token", WishlistRequest(menuId)).execute()

                // Jika status sukses (200-299)
                if (response.isSuccessful) {
                    // Simpan ke SQL Lokal (Room)
                    val dataToSave = WishlistModel(
                        id = menuId.hashCode(), // Menggunakan hash agar unik
                        user_id = 0,
                        menu_id = menuId,
                        name = name,
                        price = price,
                        imageUrl = imageUrl
                    )
                    wishlistDao.addWishlist(dataToSave)
                    Log.d("API_DEBUG", "SUKSES: Data masuk Laravel & Lokal untuk MenuID: $menuId")
                } else {
                    Log.e("API_DEBUG", "SERVER MENOLAK: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_DEBUG", "LOGISTIK ERROR: ${e.message}")
            }
        }
    }

    fun removeFromWishlist(menuId: String) {
        Log.d("API_DEBUG", "Fungsi removeFromWishlist dipanggil untuk MenuID: $menuId") // Tambahkan ini
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = TokenManager.instance.getToken()
                if (token.isNullOrEmpty()) {
                    Log.e("API_DEBUG", "Hapus gagal: Token kosong!")
                    return@launch
                }

                val tokenHeader = "Bearer $token"
                Log.d("API_DEBUG", "Mengirim request DELETE ke Laravel...")

                val response = apiService.removeFromWishlist(tokenHeader, menuId).execute()

                if (response.isSuccessful) {
                    // 2. Jika di MySQL Laravel sukses terhapus, baru hapus di SQL Lokal (Room)
                    wishlistDao.deleteByMenuId(menuId)
                    Log.d("API_DEBUG", "BERHASIL: Terhapus di Laravel & Lokal. MenuID: $menuId")
                } else {
                    // Jika server menolak (misal: 404 Not Found atau 401 Unauthorized)
                    Log.e("API_DEBUG", "SERVER GAGAL HAPUS: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API_DEBUG", "KONEKSI ERROR SAAT HAPUS: ${e.message}")
            }
        }
    }
}
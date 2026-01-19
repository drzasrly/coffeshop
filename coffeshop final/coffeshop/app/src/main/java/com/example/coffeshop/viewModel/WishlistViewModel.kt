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

    private val apiService = RetrofitClient.instance
    private val wishlistDao = AppDatabase.getDatabase(application).wishlistDao()

    val localWishlist = wishlistDao.getAllWishlist().asLiveData()

    fun addProductToWishlist(menuId: String, name: String, price: String, imageUrl: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // PERBAIKAN: Hapus parameter "Bearer $token"
                // Hanya kirim objek WishlistRequest(menuId)
                val response = apiService.addWishlist(WishlistRequest(menuId)).execute()

                if (response.isSuccessful) {
                    val dataToSave = WishlistModel(
                        id = menuId.hashCode(),
                        user_id = 0,
                        menu_id = menuId,
                        name = name,
                        price = price,
                        imageUrl = imageUrl
                    )
                    wishlistDao.addWishlist(dataToSave)
                    Log.d("API_DEBUG", "SUKSES: Masuk Laravel & Lokal untuk MenuID: $menuId")
                } else {
                    Log.e("API_DEBUG", "SERVER MENOLAK: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_DEBUG", "LOGISTIK ERROR: ${e.message}")
            }
        }
    }

    fun removeFromWishlist(menuId: String) {
        Log.d("API_DEBUG", "Fungsi removeFromWishlist dipanggil untuk MenuID: $menuId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // PERBAIKAN: Hapus parameter tokenHeader manual
                // Sekarang hanya mengirim parameter menuId saja
                val response = apiService.removeFromWishlist(menuId).execute()

                if (response.isSuccessful) {
                    wishlistDao.deleteByMenuId(menuId)
                    Log.d("API_DEBUG", "BERHASIL: Terhapus di Laravel & Lokal. MenuID: $menuId")
                } else {
                    Log.e("API_DEBUG", "SERVER GAGAL HAPUS: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_DEBUG", "KONEKSI ERROR SAAT HAPUS: ${e.message}")
            }
        }
    }
}
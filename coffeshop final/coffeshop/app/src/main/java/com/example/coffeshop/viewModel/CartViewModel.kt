package com.example.coffeshop.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.coffeshop.AppDatabase
import com.example.coffeshop.Domain.CartModel
import com.example.coffeshop.Domain.CartRequest
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.Repository.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.apiService
    private val cartDao = AppDatabase.getDatabase(application).cartDao()

    // Ambil data dari database Room
    val localCart = cartDao.getAllCart().asLiveData()

    fun addToCart(
        menuId: String,
        name: String,
        price: String,
        imageUrl: String?,
        qty: Int,
        size: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1️⃣ LANGSUNG SIMPAN KE LOKAL (Agar UI tidak kosong)
            val dataToSave = CartModel(
                menu_id = menuId,
                name = name,
                price = price,
                imageUrl = imageUrl,
                quantity = qty,
                size = size
            )

            try {
                // Simpan ke Room
                cartDao.addCart(dataToSave)
                Log.d("CART_DEBUG", "LOKAL: Berhasil simpan ke Room")

                // 2️⃣ BARU SINKRON KE SERVER
                val token = TokenManager.instance.getToken()
                val response = apiService.addCart(
                    "Bearer $token",
                    CartRequest(menuId, qty, size)
                ).execute()

                if (response.isSuccessful) {
                    Log.d("CART_DEBUG", "SERVER: Berhasil sinkron ke API")
                } else {
                    Log.e("CART_DEBUG", "SERVER GAGAL: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Jika internet mati, data tetap ada di lokal (Room)
                Log.e("CART_DEBUG", "NETWORK ERROR: ${e.message}")
            }
        }
    }

    fun removeFromCart(menuId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Hapus lokal dulu agar user merasa cepat
                cartDao.deleteByMenuId(menuId)

                val token = TokenManager.instance.getToken()
                val response = apiService.removeFromCart("Bearer $token", menuId).execute()

                if (!response.isSuccessful) {
                    Log.e("CART_DEBUG", "Gagal hapus di server")
                }
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "ERROR remove: ${e.message}")
            }
        }
    }

    fun updateQuantity(menuId: String, localId: Int, newQty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Update lokal (Room) segera
                cartDao.updateQuantity(localId, newQty)

                // 2. Update server
                val token = TokenManager.instance.getToken()
                val response = apiService.updateCartQuantity(
                    "Bearer $token",
                    menuId,
                    CartRequest(menuId, newQty, "")
                ).execute()

                if (response.isSuccessful) {
                    Log.d("CART_DEBUG", "SERVER: Qty terupdate ke $newQty")
                }
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "ERROR update qty: ${e.message}")
            }
        }
    }
    fun clearSelectedItems(items: List<CartModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            val ids = items.map { it.menu_id }
            // Panggil fungsi delete dari DAO/Repository Anda
            // Contoh: repository.deleteCheckoutedItems(ids)
            cartDao.deleteCheckoutedItems(ids)
        }
    }
}
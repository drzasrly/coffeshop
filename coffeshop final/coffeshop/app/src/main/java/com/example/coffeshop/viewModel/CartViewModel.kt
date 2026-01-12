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

    // ✅ SUDAH BENAR
    val localCart = cartDao.getAllCart().asLiveData()

    // ✅ SUDAH BENAR
    fun addToCart(
        menuId: String,
        name: String,
        price: String,
        imageUrl: String?,
        qty: Int,
        size: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = TokenManager.instance.getToken()
                val response = apiService.addCart(
                    "Bearer $token",
                    CartRequest(menuId, qty, size)
                ).execute()

                if (response.isSuccessful) {
                    val dataToSave = CartModel(
                        menu_id = menuId,
                        name = name,
                        price = price,
                        imageUrl = imageUrl,
                        quantity = qty,
                        size = size
                    )
                    cartDao.addCart(dataToSave)
                    Log.d("CART_DEBUG", "SUKSES add cart")
                }
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "ERROR add cart: ${e.message}")
            }
        }
    }

    // ✅ SUDAH BENAR
    fun removeFromCart(menuId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = TokenManager.instance.getToken()
                val response =
                    apiService.removeFromCart("Bearer $token", menuId).execute()

                if (response.isSuccessful) {
                    cartDao.deleteByMenuId(menuId)
                }
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "ERROR remove: ${e.message}")
            }
        }
    }

    // ✅ INI YANG PENTING (TIDAK MENGHAPUS YANG LAMA)
    fun updateQuantity(menuId: String, localId: Int, newQty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1️⃣ Update LOCAL (Room) → sumber utama UI
                cartDao.updateQuantity(localId, newQty)

                // 2️⃣ Update SERVER (SET quantity, BUKAN tambah)
                val token = TokenManager.instance.getToken()
                apiService.updateCartQuantity(
                    "Bearer $token",
                    menuId,
                    CartRequest(menuId, newQty, "")
                ).execute()

                Log.d("CART_DEBUG", "Qty SET -> $newQty")
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "ERROR update qty: ${e.message}")
            }
        }
    }

}

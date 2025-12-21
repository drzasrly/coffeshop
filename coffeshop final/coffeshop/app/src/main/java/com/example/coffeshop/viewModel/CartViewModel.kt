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

    val localCart = cartDao.getAllCart().asLiveData()

    fun addToCart(menuId: String, name: String, price: String, imageUrl: String?, qty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = TokenManager.instance.getToken()
                val response = apiService.addCart("Bearer $token", CartRequest(menuId, qty)).execute()

                if (response.isSuccessful) {
                    val dataToSave = CartModel(
                        menu_id = menuId,
                        name = name,
                        price = price,
                        imageUrl = imageUrl,
                        quantity = qty
                    )
                    cartDao.addCart(dataToSave)
                    Log.d("CART_DEBUG", "SUKSES: Masuk Cart Laravel & Lokal")
                }
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "ERROR: ${e.message}")
            }
        }
    }

    // Tambahkan ini di CartViewModel.kt
    fun fetchCartFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = TokenManager.instance.getToken()
                val response = apiService.getCart("Bearer $token").execute()
                if (response.isSuccessful) {
                    // Parse response body dan simpan ke Room
                    // (Logika parsing tergantung format JSON dari Laravel Anda)
                    Log.d("CART_DEBUG", "Berhasil sinkron data dari server ke lokal")
                }
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "Gagal fetch data: ${e.message}")
            }
        }
    }

    fun removeFromCart(menuId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = TokenManager.instance.getToken()
                val response = apiService.removeFromCart("Bearer $token", menuId).execute()
                if (response.isSuccessful) {
                    cartDao.deleteByMenuId(menuId)
                }
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "ERROR HAPUS: ${e.message}")
            }
        }
    }
}
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

    // UPDATE: Gunakan .instance sesuai file RetrofitClient kamu
    private val apiService = RetrofitClient.instance
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
            val dataToSave = CartModel(
                menu_id = menuId,
                name = name,
                price = price,
                imageUrl = imageUrl,
                quantity = qty,
                size = size
            )

            try {
                cartDao.addCart(dataToSave)

                // PERBAIKAN: Hapus parameter "Bearer $token" karena sudah dihandle Interceptor
                val response = apiService.addCart(
                    CartRequest(menuId, qty, size)
                ).execute()

                if (response.isSuccessful) {
                    Log.d("CART_DEBUG", "SERVER: Berhasil sinkron")
                }
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "ERROR: ${e.message}")
            }
        }
    }

    fun removeFromCart(menuId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartDao.deleteByMenuId(menuId)

                // PERBAIKAN: Hapus parameter "Bearer $token"
                val response = apiService.removeFromCart(menuId).execute()

                if (!response.isSuccessful) Log.e("CART_DEBUG", "Gagal hapus di server")
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "ERROR remove: ${e.message}")
            }
        }
    }

    fun updateQuantity(menuId: String, localId: Int, newQty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Update Room
                cartDao.updateQuantity(localId, newQty)

                // 2. Update server
                // PERBAIKAN: Hapus parameter "Bearer $token"
                // Pastikan CartRequest mengirim data yang dibutuhkan backend (biasanya size dikosongkan jika tidak update size)
                val response = apiService.updateCartQuantity(
                    menuId,
                    CartRequest(menuId, newQty, "")
                ).execute()

                if (response.isSuccessful) {
                    Log.d("CART_DEBUG", "SERVER: Qty terupdate")
                }
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "ERROR update qty: ${e.message}")
            }
        }
    }

    fun clearSelectedItems(items: List<CartModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Mengambil semua menu_id dari list item yang dicheckout
                val ids = items.map { it.menu_id }

                // 1. Hapus dari database lokal (Room)
                cartDao.deleteCheckoutedItems(ids)

                // 2. Opsi: Jika API kamu butuh sinkronisasi penghapusan setelah checkout,
                // kamu bisa menambahkan loop panggil apiService.removeFromCart(id) di sini.

                Log.d("CART_DEBUG", "LOKAL: Item yang dicheckout berhasil dihapus dari keranjang")
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "Error clearing selected items: ${e.message}")
            }
        }
    }
}
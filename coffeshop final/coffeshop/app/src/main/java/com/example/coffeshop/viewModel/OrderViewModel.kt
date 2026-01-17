package com.example.coffeshop.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeshop.Domain.OrderItemRequest
import com.example.coffeshop.Domain.OrderRequest
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.Repository.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    // PERBAIKAN: Ganti .apiService menjadi .instance
    private val apiService = RetrofitClient.instance

    fun checkout(
        items: List<OrderItemRequest>,
        totalPrice: Double,
        address: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = TokenManager.instance.getToken()

                if (token.isNullOrEmpty()) {
                    withContext(Dispatchers.Main) {
                        onError("Token tidak ditemukan, silakan login ulang")
                    }
                    return@launch
                }

                val request = OrderRequest(
                    total_price = totalPrice,
                    address = address,
                    items = items
                )

                // Memanggil API via instance Retrofit yang benar
                val response = apiService.createOrder(
                    "Bearer $token",
                    request
                )

                if (response.isSuccessful) {
                    Log.d("ORDER_DEBUG", "Checkout berhasil")

                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Checkout gagal"
                    Log.e("ORDER_DEBUG", errorMsg)

                    withContext(Dispatchers.Main) {
                        onError(errorMsg)
                    }
                }

            } catch (e: Exception) {
                Log.e("ORDER_DEBUG", "Error checkout", e)

                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Terjadi kesalahan")
                }
            }
        }
    }
}
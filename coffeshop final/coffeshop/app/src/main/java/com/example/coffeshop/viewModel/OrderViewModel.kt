package com.example.coffeshop.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.coffeshop.Domain.*
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.Repository.RetrofitClient
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.instance

    private val _firebaseOrders = MutableLiveData<List<OrderModel>>()
    val firebaseOrders: LiveData<List<OrderModel>> get() = _firebaseOrders

    fun checkout(
        items: List<OrderItemRequest>,
        totalPrice: Double,
        address: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Kita tidak perlu mengambil token di sini untuk dikirim ke API
                // karena AuthInterceptor di RetrofitClient sudah melakukannya.

                val request = OrderRequest(
                    total_price = totalPrice,
                    address = address,
                    items = items
                )

                // PERBAIKAN: Hapus parameter "Bearer $token"
                // Sekarang hanya mengirim objek 'request' saja
                val response = apiService.createOrder(request)

                if (response.isSuccessful) {
                    val orderId = response.body()?.data?.id ?: 0

                    withContext(Dispatchers.Main) {
                        if (orderId != 0) {
                            onSuccess(orderId)
                        } else {
                            onError("Server sukses tapi gagal memberikan ID Pesanan")
                        }
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Gagal memproses di server"
                    withContext(Dispatchers.Main) {
                        onError(errorMsg)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Koneksi bermasalah, silakan coba lagi")
                }
            }
        }
    }

    fun fetchOrdersFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Orders")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<OrderModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(OrderModel::class.java)
                    item?.let {
                        it.orderId = childSnapshot.key ?: ""
                        list.add(it)
                    }
                }

                val sortedList = list.sortedByDescending { it.timestamp }
                _firebaseOrders.postValue(sortedList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderViewModel", "Firebase Error: ${error.message}")
            }
        })
    }
}
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

/**
 * ViewModel untuk mengelola pesanan.
 * Menghubungkan API Laravel (MySQL) dan Firebase Realtime Database.
 */
class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.instance

    // LiveData untuk daftar pesanan dari Firebase
    private val _firebaseOrders = MutableLiveData<List<OrderModel>>()
    val firebaseOrders: LiveData<List<OrderModel>> get() = _firebaseOrders

    /**
     * Fungsi Checkout: Mengirim data ke MySQL dan mengambil ID resmi untuk Firebase.
     * Perbaikan: Mengambil ID dari objek 'data' sesuai struktur OrderController.php terbaru.
     */
    fun checkout(
        items: List<OrderItemRequest>,
        totalPrice: Double,
        address: String,
        onSuccess: (Int) -> Unit, // Callback membawa ID MySQL
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

                // 1. Simpan ke Laravel API (MySQL)
                val response = apiService.createOrder("Bearer $token", request)

                if (response.isSuccessful) {
                    // PENTING: Ambil ID dari response.body()?.data?.id
                    val orderId = response.body()?.data?.id ?: 0

                    withContext(Dispatchers.Main) {
                        if (orderId != 0) {
                            // Kirim ID ke Activity untuk dijadikan key di Firebase
                            onSuccess(orderId)
                        } else {
                            // Mencegah error "ID tidak valid" yang merusak sinkronisasi
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

    /**
     * Memantau status pesanan secara Real-time dari Firebase.
     * Digunakan oleh OrderActivity untuk melihat perubahan status dari Admin Filament.
     */
    fun fetchOrdersFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Orders")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<OrderModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(OrderModel::class.java)
                    item?.let {
                        // Pastikan ID Firebase tersimpan di objek model
                        it.orderId = childSnapshot.key ?: ""
                        list.add(it)
                    }
                }

                // Urutkan berdasarkan timestamp terbaru
                val sortedList = list.sortedByDescending { it.timestamp }
                _firebaseOrders.postValue(sortedList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderViewModel", "Firebase Error: ${error.message}")
            }
        })
    }
}
// viewModel/LoginViewModel.kt
package com.example.coffeshop.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // <-- WAJIB: Memungkinkan Coroutine
import com.example.coffeshop.Domain.LoginRequest
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.Repository.RetrofitClient // <-- Wajib impor RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Menggunakan ApiService dari RetrofitClient
    private val apiService = RetrofitClient.apiService

    // LiveData untuk status Login (SUCCESS atau pesan Error)
    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> = _loginResult

    fun login(email: String, password: String) {

        // Meluncurkan Coroutine di scope ViewModel
        viewModelScope.launch {
            try {
                // 1. Buat Request dan Kirim ke Laravel API
                val request = LoginRequest(email, password)
                val response = apiService.loginUser(request) // Panggilan Retrofit

                // 2. Cek respons dari Laravel
                if (response.success && response.access_token.isNotEmpty()) {
                    // 3. Simpan Token JWT menggunakan TokenManager
                    TokenManager.instance.saveToken(response.access_token)
                    _loginResult.value = "SUCCESS"
                } else {
                    // Jika success=true tetapi token kosong, atau logic lain di Laravel
                    _loginResult.value = "Gagal Login: Respons server tidak valid."
                }
            } catch (e: retrofit2.HttpException) {
                // Menangani error HTTP spesifik (misal 401 Unauthorized dari Laravel)
                if (e.code() == 401) {
                    _loginResult.value = "Kredensial salah. Cek email dan password."
                } else {
                    _loginResult.value = "Error Server: Kode ${e.code()}"
                }
            }
            catch (e: Exception) {
                // Tangani Error Jaringan umum (Timeout, No Internet, dll.)
                _loginResult.value = "Error Jaringan: Gagal terhubung ke server. Pastikan server aktif (10.0.2.2)."
            }
        }
    }
}
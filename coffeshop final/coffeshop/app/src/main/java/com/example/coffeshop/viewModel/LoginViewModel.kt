package com.example.coffeshop.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeshop.Domain.LoginRequest
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.Repository.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // PERBAIKAN: Ganti .apiService menjadi .instance
    private val apiService = RetrofitClient.instance

    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                // Melakukan request login ke server
                val response = apiService.loginUser(LoginRequest(email.trim(), password.trim()))

                // Mengambil token dari field 'accessToken'
                val tokenDiterima = response.accessToken

                if (!tokenDiterima.isNullOrEmpty()) {

                    // 1. Simpan Token ke SharedPreferences
                    TokenManager.instance.saveToken(tokenDiterima, 3600)

                    // 2. Simpan Data User (Nama & Email)
                    response.user?.let { userData ->
                        userData.name?.let { TokenManager.instance.saveUserName(it) }
                        userData.email?.let { TokenManager.instance.saveUserEmail(it) }
                    }

                    // Memberitahu Activity bahwa login berhasil
                    _loginResult.value = "SUCCESS"

                } else {
                    _loginResult.value = "Gagal: Token tidak ditemukan dalam respon server"
                }

            } catch (e: Exception) {
                Log.e("LOGIN_ERROR", "Detail Error: ${e.message}")
                _loginResult.value = "Error: ${e.message}"
            }
        }
    }
}
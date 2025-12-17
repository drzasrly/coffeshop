// File: viewModel/LoginViewModel.kt
package com.example.coffeshop.viewModel

import android.util.Log
import LoginResponse // Pastikan ini mengarah ke Data Class yang sudah diperbarui dengan 'val user: User?'
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeshop.Domain.LoginRequest
import com.example.coffeshop.Helper.TokenManager
import com.example.coffeshop.Repository.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService
    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.loginUser(LoginRequest(email, password))
                if (response.success && response.access_token.isNotEmpty()) {

                    // Simpan Token & Kadaluarsa
                    TokenManager.instance?.saveToken(response.access_token, response.expires_in)
                    response.user?.let { userData ->
                        TokenManager.instance?.saveUserName(userData.name)
                        TokenManager.instance?.saveUserEmail(userData.email)
                    }
                    // Simpan Nama User
                    response.user?.name?.let { TokenManager.instance?.saveUserName(it) }

                    _loginResult.value = "SUCCESS"
                } else {
                    _loginResult.value = "Gagal Login"
                }
            } catch (e: Exception) {
                _loginResult.value = "Error: ${e.message}"
            }
        }
    }
}
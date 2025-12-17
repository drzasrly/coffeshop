// File: Helper/TokenManager.kt
package com.example.coffeshop.Helper

import android.content.Context
import android.content.SharedPreferences

class TokenManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile private lateinit var INSTANCE: TokenManager

        // Definisikan kunci di satu tempat untuk konsistensi
        private const val JWT_TOKEN_KEY = "JWT_TOKEN"
        private const val USER_NAME_KEY = "USER_NAME"

        private const val USER_EMAIL_KEY = "USER_EMAIL"

        fun initialize(context: Context) {
            synchronized(this) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = TokenManager(context.applicationContext)
                }
            }
        }

        val instance: TokenManager
            get() {
                if (!::INSTANCE.isInitialized) {
                    throw IllegalStateException("TokenManager must be initialized in the Application class.")
                }
                return INSTANCE
            }
    }


// Helper/TokenManager.kt

    fun saveToken(token: String, expiresIn: Int) {
        val currentTime = System.currentTimeMillis() / 1000 // Satuan Detik
        prefs.edit().apply {
            putString("JWT_TOKEN", token)
            putLong("LOGIN_TIME", currentTime)
            putInt("EXPIRES_IN", expiresIn)
            apply()
        }
    }

    fun isTokenExpired(): Boolean {
        val loginTime = prefs.getLong("LOGIN_TIME", 0)
        val expiresIn = prefs.getInt("EXPIRES_IN", 0)
        val currentTime = System.currentTimeMillis() / 1000

        // Jika waktu sekarang melebihi waktu login + durasi token
        return currentTime > (loginTime + expiresIn)
    }



    // --- Fungsi Token ---
    fun saveToken(token: String) {
        prefs.edit().putString(JWT_TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(JWT_TOKEN_KEY, null)
    }

    // --- Fungsi Nama Pengguna ---

    fun saveUserName(name: String) {
        prefs.edit().putString(USER_NAME_KEY, name).apply()
    }

    fun getUserName(): String? {
        return prefs.getString(USER_NAME_KEY, null)
    }




    fun saveUserEmail(email: String) {
        prefs.edit().putString(USER_EMAIL_KEY, email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(USER_EMAIL_KEY, null)
    }


    // --- Clear Token ---

    fun clearToken() {
        prefs.edit()
            .remove(JWT_TOKEN_KEY)
            .remove(USER_NAME_KEY)
            .remove(USER_EMAIL_KEY)
            .apply()
    }
}
package com.example.coffeshop.Helper

import android.content.Context
import android.content.SharedPreferences

class TokenManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile private lateinit var INSTANCE: TokenManager

        private const val JWT_TOKEN_KEY = "JWT_TOKEN"
        private const val USER_NAME_KEY = "USER_NAME"
        private const val USER_EMAIL_KEY = "USER_EMAIL"
        private const val LOGIN_TIME_KEY = "LOGIN_TIME"
        private const val EXPIRES_IN_KEY = "EXPIRES_IN"

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
                    throw IllegalStateException("TokenManager must be initialized.")
                }
                return INSTANCE
            }
    }

    // --- Token & Session ---
    fun saveToken(token: String, expiresIn: Int) {
        val currentTime = System.currentTimeMillis() / 1000
        prefs.edit().apply {
            putString(JWT_TOKEN_KEY, token)
            putLong(LOGIN_TIME_KEY, currentTime)
            putInt(EXPIRES_IN_KEY, expiresIn)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(JWT_TOKEN_KEY, null)

    fun isTokenExpired(): Boolean {
        val loginTime = prefs.getLong(LOGIN_TIME_KEY, 0)
        val expiresIn = prefs.getInt(EXPIRES_IN_KEY, 0)
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime > (loginTime + expiresIn)
    }

    // --- User Info (Ini yang tadi bikin error) ---
    fun saveUserName(name: String) {
        prefs.edit().putString(USER_NAME_KEY, name).apply()
    }

    fun getUserName(): String? = prefs.getString(USER_NAME_KEY, "User")

    fun saveUserEmail(email: String) {
        prefs.edit().putString(USER_EMAIL_KEY, email).apply()
    }

    fun getUserEmail(): String? = prefs.getString(USER_EMAIL_KEY, null)

    // --- Clear Total ---
    fun clearToken() {
        prefs.edit().clear().apply()
    }
}
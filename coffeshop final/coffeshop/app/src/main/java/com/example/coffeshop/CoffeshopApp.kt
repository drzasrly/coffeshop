package com.example.coffeshop

import android.app.Application
import android.util.Log
import com.example.coffeshop.Helper.TokenManager
import com.google.firebase.FirebaseApp


class CoffeshopApp : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            // 🟢 Titik Inisialisasi Kritis
            TokenManager.initialize(applicationContext)

            // Inisialisasi Firebase
            FirebaseApp.initializeApp(this)

        } catch (e: Exception) {
            // Log jika terjadi crash FATAL di awal
            Log.e("APP_INIT", "Fatal error during Application onCreate: ${e.message}", e)
        }
    }
}
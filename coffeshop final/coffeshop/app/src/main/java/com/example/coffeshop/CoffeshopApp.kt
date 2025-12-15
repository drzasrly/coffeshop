package com.example.coffeshop

import android.app.Application
import com.example.coffeshop.Helper.TokenManager
import com.google.firebase.FirebaseApp

class CoffeshopApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.initialize(applicationContext)
        FirebaseApp.initializeApp(this)
    }
}
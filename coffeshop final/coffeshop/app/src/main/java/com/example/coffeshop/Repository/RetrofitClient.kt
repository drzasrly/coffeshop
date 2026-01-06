package com.example.coffeshop.Repository

import com.example.coffeshop.Helper.AuthInterceptor
import com.example.coffeshop.Helper.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Gunakan 10.0.2.2 jika Anda menggunakan Emulator Android bawaan.
    // IP 192.168.x.x hanya digunakan jika Anda pakai HP Fisik.
    private const val BASE_URL = "http://192.168.110.226:8000/api/"

    private val client: OkHttpClient by lazy {
        // Logging Interceptor sangat penting agar error API muncul di Logcat
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging) // Menampilkan detail request/response di Logcat
            .addInterceptor(AuthInterceptor(TokenManager.instance))
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
package com.example.coffeshop.Repository

import com.example.coffeshop.Helper.AuthInterceptor
import com.example.coffeshop.Helper.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.3:8000/api/"

    private val client: OkHttpClient
        get() {
            return OkHttpClient.Builder()
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
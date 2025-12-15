// Helper/AuthInterceptor.kt
package com.example.coffeshop.Helper

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        val newRequest = originalRequest.newBuilder()

        if (token != null) {
            newRequest.addHeader("Authorization", "Bearer $token")
        }

        newRequest.addHeader("Accept", "application/json")

        return chain.proceed(newRequest.build())
    }
}
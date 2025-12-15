package com.example.coffeshop.Domain

data class LoginResponse(
    val success: Boolean,
    val access_token: String,
    val token_type: String = "bearer",
    val expires_in: Int,
)